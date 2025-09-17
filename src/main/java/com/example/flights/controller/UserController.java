package com.example.flights.controller;

import java.security.Principal;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
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
    public Mono<UserModel> getUserInfo(Principal principal)
    {
        String email = extractEmail(principal);

        return userRepository.findById(email)
        .switchIfEmpty(userRepository.save(new UserModel(email)));
    }

    @PostMapping("users/subscribe")
    public Mono<UserModel> subscribe(Principal principal, @RequestBody SubscriptionModel subscription)
    {
        String email = extractEmail(principal);

        return userRepository.findById(email)
        .switchIfEmpty(userRepository.save(new UserModel(email)))
        .flatMap(user -> {
            user.getSubscriptions().add(subscription);
            return userRepository.save(user);
        })
        .flatMap(savedUser ->
            emailSenderService
                .sendConfirmationEmailAsync(
                    email,
                    subscription.getAirline_code(),
                    subscription.getFlight_number(),
                    subscription.getCity_en()
                )
                .thenReturn(savedUser)
        );
    }

    @PostMapping("users/unsubscribe")
    public Mono<Void> deleteUserSubscription(Principal principal,
                                        @RequestParam String airline_code,
                                        @RequestParam String flight_number,
                                        @RequestParam String scheduled_date)
    {
        String email = extractEmail(principal);

        return userRepository.findById(email)
        .flatMap(user -> {
            user.getSubscriptions().removeIf(sub -> 
                sub.getAirline_code().equals(airline_code) &&
                sub.getFlight_number().equals(flight_number) &&
                sub.getScheduled_time().contains(scheduled_date)
            );
            return userRepository.save(user);
        })
        .then();
    }

    private String extractEmail(Principal principal)
    {
        if (principal instanceof OAuth2AuthenticationToken oauth)
        {
            Object email = oauth.getPrincipal().getAttributes().get("email");
            return email != null ? email.toString() : oauth.getName();
        }

        if (principal instanceof JwtAuthenticationToken jwt)
        {
            Jwt token = jwt.getToken();
            Object email = token.getClaim("email");
            return email != null ? email.toString() : token.getSubject();
        }

        return principal.getName();
    }
}
