package com.example.reactivewings.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class BenGurionAPI {
    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public BenGurionAPI(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://data.gov.il").build();
        this.objectMapper = objectMapper;
    }

    public Mono<JsonNode> getBenGurionFlights() {
        return webClient.get()
                .uri("/api/3/action/datastore_search?resource_id=e83f763b-b7d7-479e-b172-ae981ddc6de5")
                .retrieve()
                .bodyToMono(String.class)
                .map(body -> {
                    try {
                        return objectMapper.readTree(body);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to parse response", e);
                    }
                });
    }
}
