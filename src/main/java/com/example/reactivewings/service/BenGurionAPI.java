package com.example.reactivewings.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;

@Service
public class BenGurionAPI {
    private final WebClient webClient;

    public BenGurionAPI(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://data.gov.il").build();
    }

    public Flux<JsonNode> getBenGurionFlightRecords() {
        try {
            return webClient.get()
                    .uri("/api/3/action/datastore_search?resource_id=e83f763b-b7d7-479e-b172-ae981ddc6de5")
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .map(response -> response.path("result").path("records"))
                    .flatMapMany(records -> Flux.fromIterable(records));
        } catch (Exception e) {
            return Flux.error(new RuntimeException("Failed to fetch flights", e));
        }
    }
}
