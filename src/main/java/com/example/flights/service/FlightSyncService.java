package com.example.flights.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.flights.model.FlightModel;
import com.example.flights.repo.FlightRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;

@Service
public class FlightSyncService
{
    private final FlightRepository flightRepository;
    private final BenGurionAPI bgnAPI;
    private final ObjectMapper objectMapper;

    public FlightSyncService(FlightRepository flightRepository, BenGurionAPI bgnAPI, ObjectMapper objectMapper)
    {
        this.flightRepository = flightRepository;
        this.bgnAPI = bgnAPI;
        this.objectMapper = objectMapper;
    }

    @Scheduled(cron = "0 1/1 * * * *")
    public void syncFlightsFromAPI()
    {
        System.out.println("Synchronizing flight data from Ben Gurion API.");
        
        bgnAPI.getBenGurionFlights()
              .map(data -> data.path("result").path("records"))
              .flatMapMany(records -> Flux.fromIterable(records)
                                          .map(record -> objectMapper.convertValue(record, FlightModel.class)))
              .collectList()
              .flatMapMany(flightList -> flightRepository.deleteAll()
                                                         .thenMany(flightRepository.saveAll(flightList)))
              .doOnError(error -> System.err.println("Error saving flights: " + error.getMessage()))
              .doOnComplete(() -> System.out.println("Flight data synchronized successfully."))
              .subscribe();
    }
}
