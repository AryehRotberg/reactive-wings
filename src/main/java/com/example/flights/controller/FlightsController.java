package com.example.flights.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.flights.repo.FlightRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;

@RestController
public class FlightsController
{
    private final FlightRepository flightRepository;
    private final ObjectMapper objectMapper;

    public FlightsController(FlightRepository flightRepository, ObjectMapper objectMapper)
    {
        this.flightRepository = flightRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping("flights/search")
    public Mono<ResponseEntity<List<JsonNode>>> searchFlight(
        @RequestParam(required=false) String airline_code,
        @RequestParam(required=false) String flight_number,
        @RequestParam(required=false) String scheduled_date,
        @RequestParam(required=false) String scheduled_time,
        @RequestParam(required=false) String planned_date,
        @RequestParam(required=false) String planned_time
    )
    {
        return flightRepository.findAll()
            .filter(flight -> airline_code == null || flight.getAirline_code().equals(airline_code))
            .filter(flight -> flight_number == null || flight.getFlight_number().equals(flight_number))
            .filter(flight -> scheduled_date == null || flight.getScheduled_time().contains(scheduled_date))
            .filter(flight -> scheduled_time == null || flight.getScheduled_time().contains(scheduled_time))
            .filter(flight -> planned_date == null || flight.getPlanned_time().contains(planned_date))
            .filter(flight -> planned_time == null || flight.getPlanned_time().contains(planned_time))
            .map(flight -> objectMapper.convertValue(flight, JsonNode.class))
            .collectList()
            .map(results -> ResponseEntity.ok().body(results));
    }
}
