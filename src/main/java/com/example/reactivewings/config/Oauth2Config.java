package com.example.reactivewings.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;

@Configuration
public class Oauth2Config {
    @Bean
    public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(
            ReactiveClientRegistrationRepository clientRegistrationRepository) {
        DefaultServerOAuth2AuthorizationRequestResolver resolver = new DefaultServerOAuth2AuthorizationRequestResolver(
                clientRegistrationRepository);

        return new ServerOAuth2AuthorizationRequestResolver() {
            @Override
            public reactor.core.publisher.Mono<OAuth2AuthorizationRequest> resolve(
                    org.springframework.web.server.ServerWebExchange exchange, String clientRegistrationId) {
                return resolver.resolve(exchange, clientRegistrationId)
                        .map(this::addPromptLogin);
            }

            @Override
            public reactor.core.publisher.Mono<OAuth2AuthorizationRequest> resolve(
                    org.springframework.web.server.ServerWebExchange exchange) {
                return resolver.resolve(exchange)
                        .map(this::addPromptLogin);
            }

            private OAuth2AuthorizationRequest addPromptLogin(OAuth2AuthorizationRequest req) {
                return OAuth2AuthorizationRequest.from(req)
                        .additionalParameters(params -> params.put("prompt", "login"))
                        .build();
            }
        };
    }
}
