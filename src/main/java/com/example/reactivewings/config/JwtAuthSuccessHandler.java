package com.example.reactivewings.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthSuccessHandler implements ServerAuthenticationSuccessHandler {
    
    private final JwtUtil jwtUtil;
    
    public JwtAuthSuccessHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        OAuth2AuthenticatedPrincipal principal = (OAuth2AuthenticatedPrincipal) authentication.getPrincipal();

        String email = principal.getAttribute("email");
        String name = principal.getAttribute("name");
        String sub = principal.getAttribute("sub");

        Map<String, Object> claims = new HashMap<>();
        if (email != null) claims.put("email", email);
        if (name != null) claims.put("name", name);
        if (sub != null) claims.put("sub", sub);

        String subject = (email != null && !email.isBlank()) ? email : authentication.getName();

        String jwt = jwtUtil.generateToken(claims, subject);

        String redirectUrl = "https://reactivewings.vercel.app/dashboard?token=" + jwt;
        var response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.FOUND);
        response.getHeaders().set("Location", redirectUrl);
        return response.setComplete();
    }
}
