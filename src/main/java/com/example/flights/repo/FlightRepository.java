package com.example.flights.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.example.flights.model.FlightModel;


public interface FlightRepository extends ReactiveMongoRepository<FlightModel, String> {

}
