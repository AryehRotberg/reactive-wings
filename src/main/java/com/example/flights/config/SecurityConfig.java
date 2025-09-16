package com.example.flights.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler;

import java.net.URI;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig
{
    private final ReactiveClientRegistrationRepository clientRegistrationRepository;

    public SecurityConfig(ReactiveClientRegistrationRepository clientRegistrationRepository)
    {
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(ReactiveClientRegistrationRepository clientRegistrationRepository)
    {
        DefaultServerOAuth2AuthorizationRequestResolver resolver = new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
        return new ServerOAuth2AuthorizationRequestResolver()
        {
            @Override
            public reactor.core.publisher.Mono<OAuth2AuthorizationRequest> resolve(org.springframework.web.server.ServerWebExchange exchange, String clientRegistrationId)
            {
                return resolver.resolve(exchange, clientRegistrationId)
                    .map(req -> OAuth2AuthorizationRequest.from(req)
                        .additionalParameters(params -> params.put("prompt", "login"))
                        .build());
            }

            @Override
            public reactor.core.publisher.Mono<OAuth2AuthorizationRequest> resolve(org.springframework.web.server.ServerWebExchange exchange)
            {
                return resolver.resolve(exchange)
                    .map(req -> OAuth2AuthorizationRequest.from(req)
                        .additionalParameters(params -> params.put("prompt", "login"))
                        .build());
            }
        };
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ReactiveClientRegistrationRepository clientRegistrationRepository)
    {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/health", "/actuator/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2.authorizationRequestResolver(authorizationRequestResolver(clientRegistrationRepository)))
            .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessHandler(logoutSuccessHandler()))
            .build();
    }

    private ServerLogoutSuccessHandler logoutSuccessHandler()
    {
        OidcClientInitiatedServerLogoutSuccessHandler logoutSuccessHandler = new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
        logoutSuccessHandler.setLogoutSuccessUrl(URI.create("/"));
        return logoutSuccessHandler;
    }
}
