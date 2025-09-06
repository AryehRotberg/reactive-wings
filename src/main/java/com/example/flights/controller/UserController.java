package com.example.flights.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.flights.model.SubscriptionModel;
import com.example.flights.model.UserModel;
import com.example.flights.repo.UserRepository;
import com.example.flights.service.EmailSenderService;

import reactor.core.publisher.Mono;


@RestController
public class UserController
{
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;

    public UserController(UserRepository userRepository, EmailSenderService emailSenderService)
    {
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
    }

    @GetMapping("users/user-info")
    public Mono<UserModel> getUserEmail(@AuthenticationPrincipal OAuth2User oauth2User)
    {
        String email = oauth2User.getAttribute("email");

        return userRepository.findById(email)
                .switchIfEmpty(
                    userRepository.save(new UserModel(email))
                );
    }

    @PostMapping("users/subscribe")
    public Mono<UserModel> subscribe(@AuthenticationPrincipal OAuth2User oauth2User, @RequestBody SubscriptionModel subscription)
    {
        String email = oauth2User.getAttribute("email");

        return userRepository.findById(email)
                .switchIfEmpty(userRepository.save(new UserModel(email)))
                .flatMap(user -> {
                    user.getSubscriptions().add(subscription);
                    return userRepository.save(user)
                    .doOnSuccess(savedUser -> 
                    {
                        emailSenderService.sendConfirmationEmailAsync(email, subscription.getAirline_code(), subscription.getFlight_number()).subscribe();
                    });
                });
    }
}
