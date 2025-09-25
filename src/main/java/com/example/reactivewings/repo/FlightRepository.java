package com.example.reactivewings.repo;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.reactivewings.model.Flight;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface FlightRepository extends ReactiveMongoRepository<Flight, String> {
    Mono<Flight> findByFlightId(Integer flightId);
    Flux<Flight> findByDirection(String direction);

    @Query("{ 'statusEn': ?0 }")
    Flux<Flight> findByStatusEn(String status);
    Mono<Long> deleteByLastUpdatedBefore(LocalDateTime cutoff);
}
