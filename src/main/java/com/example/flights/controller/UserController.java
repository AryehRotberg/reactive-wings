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

import reactor.core.publisher.Mono;


@RestController
public class UserController
{
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    @GetMapping("users/user-info")
    public Mono<UserModel> getUserEmail(@AuthenticationPrincipal OAuth2User oauth2User)
    {
        String email = oauth2User.getAttribute("email");

        return userRepository.findById(email)
                .switchIfEmpty(
                    userRepository.save(new UserModel(email))  // save new user if not exists
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
                    return userRepository.save(user);
                });
    }
    
}
