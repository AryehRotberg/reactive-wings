package com.example.reactivewings.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reactivewings.model.Flight;
import com.example.reactivewings.model.User;
import com.example.reactivewings.repo.UserRepository;
import com.example.reactivewings.service.EmailSenderService;
import com.example.reactivewings.utils.UserControllerUtils;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;

    public UserController(UserRepository userRepository,
                          EmailSenderService emailSenderService) {
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("/user-info")
    public Mono<User> getUserInfo(Principal principal) {
        String email = UserControllerUtils.extractEmail(principal);

        return userRepository.findById(email)
        .switchIfEmpty(userRepository.save(new User(email)));
    }

    @PostMapping("/subscribe")
    public Mono<User> subscribe(Principal principal, @RequestBody Flight subscription) {
        String email = UserControllerUtils.extractEmail(principal);

        return userRepository.findById(email)
        .switchIfEmpty(userRepository.save(new User(email)))
        .flatMap(user -> {
            user.getSubscriptions().add(subscription);
            return userRepository.save(user);
        })
        .flatMap(savedUser ->
            emailSenderService
                .sendConfirmationEmailAsync(
                    email,
                    subscription.getAirlineCode(),
                    subscription.getFlightNumber(),
                    subscription.getCityHe(),
                    subscription.getDirection()
                )
                .thenReturn(savedUser)
        );
    }

    @PostMapping("/unsubscribe")
    public Mono<Void> deleteUserSubscription(Principal principal,
                                            @RequestParam String airlineCode,
                                            @RequestParam String flightNumber,
                                            @RequestParam String scheduledDate) {
        String email = UserControllerUtils.extractEmail(principal);

        return userRepository.findById(email)
        .flatMap(user -> {
            user.getSubscriptions().removeIf(sub -> 
                sub.getAirlineCode().equals(airlineCode) &&
                sub.getFlightNumber().equals(flightNumber) &&
                sub.getScheduledTime().contains(scheduledDate)
            );
            return userRepository.save(user);
        })
        .then();
    }
}
