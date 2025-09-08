package com.example.flights.repo;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.flights.model.FlightModel;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface FlightRepository extends ReactiveMongoRepository<FlightModel, String>
{
    Mono<FlightModel> findByExternalId(Integer externalId);
    Flux<FlightModel> findByDirection(String direction);

    @Query("{ 'status_en': ?0 }")
    Flux<FlightModel> findByStatus_en(String status);
    Mono<Long> deleteByLastUpdatedBefore(LocalDateTime cutoff);
}
