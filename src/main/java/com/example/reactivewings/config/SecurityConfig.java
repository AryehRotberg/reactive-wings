package com.example.reactivewings.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.logout.HttpStatusReturningServerLogoutSuccessHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig
{
    private final JwtAuthSuccessHandler jwtAuthSuccessHandler;
    
    @Value("${jwt.secret}")
    private String secretKey;
    
    public SecurityConfig(JwtAuthSuccessHandler jwtAuthSuccessHandler) {
        this.jwtAuthSuccessHandler = jwtAuthSuccessHandler;
    }

    @Bean
    public ServerOAuth2AuthorizationRequestResolver authorizationRequestResolver(ReactiveClientRegistrationRepository clientRegistrationRepository) {
        DefaultServerOAuth2AuthorizationRequestResolver resolver = new DefaultServerOAuth2AuthorizationRequestResolver(clientRegistrationRepository);
        return new ServerOAuth2AuthorizationRequestResolver() {
            @Override
            public reactor.core.publisher.Mono<OAuth2AuthorizationRequest> resolve(org.springframework.web.server.ServerWebExchange exchange, String clientRegistrationId) {
                return resolver.resolve(exchange, clientRegistrationId)
                    .map(req -> OAuth2AuthorizationRequest.from(req)
                        .additionalParameters(params -> params.put("prompt", "login"))
                        .build());
            }

            @Override
            public reactor.core.publisher.Mono<OAuth2AuthorizationRequest> resolve(org.springframework.web.server.ServerWebExchange exchange) {
                return resolver.resolve(exchange)
                    .map(req -> OAuth2AuthorizationRequest.from(req)
                        .additionalParameters(params -> params.put("prompt", "login"))
                        .build());
            }
        };
    }

    @Bean
    public org.springframework.security.oauth2.jwt.ReactiveJwtDecoder jwtDecoder() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(secretKey);
        javax.crypto.SecretKey key = new javax.crypto.spec.SecretKeySpec(keyBytes, "HmacSHA256");
        return org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder.withSecretKey(key).build();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                        ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return http
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .cors(Customizer.withDefaults())
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers("/health", "/actuator/**", "/flights/**").permitAll()
                .anyExchange().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .authenticationSuccessHandler(jwtAuthSuccessHandler)
            )
            .oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()))
            .logout(logout -> logout
            .logoutSuccessHandler(new HttpStatusReturningServerLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(java.util.List.of(
            "https://reactivewings.vercel.app",
            "http://136.112.133.231.nip.io:8080",
            "http://localhost:8080",
            "http://localhost:3000"
        ));
        config.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(java.util.List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
