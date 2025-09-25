package com.example.reactivewings.utils;

import java.security.Principal;

import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class UserControllerUtils
{
    public static String extractEmail(Principal principal)
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
