package com.example.flights.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.flights.model.FlightModel;
import com.example.flights.model.SubscriptionModel;
import com.example.flights.repo.FlightRepository;
import com.example.flights.repo.UserRepository;
import com.example.flights.service.BenGurionAPI;
import com.example.flights.service.EmailSenderService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class FlightsController
{
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;

    private final BenGurionAPI bgnAPI;
    private final ObjectMapper objectMapper;

    public FlightsController(FlightRepository flightRepository, UserRepository userRepository, ObjectMapper objectMapper, BenGurionAPI bgnAPI, EmailSenderService emailSenderService)
    {
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.bgnAPI = bgnAPI;
        this.objectMapper = objectMapper;
        this.emailSenderService = emailSenderService;
    }
    
    @Scheduled(cron = "0 0/15 * * * *")
    @GetMapping("flights/sync")
    public Mono<List<FlightModel>> sync()
    {
        return bgnAPI.getBenGurionFlights()
            .map(flights ->{
                JsonNode records = flights.path("result").path("records");
                List<FlightModel> flightList = new ArrayList<>();

                for (JsonNode record : records) {
                    FlightModel flight = objectMapper.convertValue(record, FlightModel.class);
                    flightList.add(flight);
                }
                return flightList;
            })
            .flatMap(flights ->
                flightRepository.deleteAll()
                    .then(flightRepository.saveAll(flights).collectList())
            );
    }

    @Scheduled(cron = "1/10 * * * * *")
    public void syncFlights() {
        System.out.println("Syncing flight statuses...");
        
        // Use stored flight data from database instead of API call
        Flux<FlightModel> storedFlights = flightRepository.findAll();

        storedFlights.collectList().subscribe(flights -> {
            // Map stored flight data by flight key
            Map<String, FlightModel> flightMap = new HashMap<>();
            for (FlightModel flight : flights)
            {
                String key = flight.getFlight_number() + "-" +
                            flight.getAirline_code() + "-" +
                            flight.getScheduled_time() + "-" +
                            flight.getAirport_code();
                flightMap.put(key, flight);
            }

            // Fetch all users
            userRepository.findAll().subscribe(user -> {
                boolean updated = false;

                for (SubscriptionModel sub : user.getSubscriptions())
                {
                    // Create a specific key using flight identifiers including scheduled time
                    // to uniquely identify the user's specific flight
                    String key = sub.getFlight_number() + "-" +
                                sub.getAirline_code() + "-" +
                                sub.getScheduled_time() + "-" +
                                sub.getAirport_code();
                    
                    // Find matching flight data from stored flights
                    FlightModel flightData = flightMap.get(key);

                    if (flightData != null)
                    {
                        // Get current values from stored flight data
                        String newScheduledTime = flightData.getScheduled_time();
                        String newPlannedTime = flightData.getPlanned_time();
                        String newTerminal = String.valueOf(flightData.getTerminal());
                        String newCounters = flightData.getCounters();
                        String newCheckinZone = flightData.getCheckin_zone();
                        String newStatus = flightData.getStatus_en();

                        // Check for changes in any field
                        boolean hasChanges = false;
                        StringBuilder changeLog = new StringBuilder();

                        if (!newScheduledTime.equals(sub.getScheduled_time())) {
                            changeLog.append("Scheduled time: ").append(sub.getScheduled_time()).append(" -> ").append(newScheduledTime).append("; ");
                            sub.setScheduled_time(newScheduledTime);
                            hasChanges = true;
                        }

                        if (!newPlannedTime.equals(sub.getPlanned_time())) {
                            changeLog.append("Planned time: ").append(sub.getPlanned_time()).append(" -> ").append(newPlannedTime).append("; ");
                            sub.setPlanned_time(newPlannedTime);
                            hasChanges = true;
                        }

                        if (!newTerminal.equals(sub.getTerminal())) {
                            changeLog.append("Terminal: ").append(sub.getTerminal()).append(" -> ").append(newTerminal).append("; ");
                            sub.setTerminal(newTerminal);
                            hasChanges = true;
                        }

                        if (!newCounters.equals(sub.getCounters())) {
                            changeLog.append("Counters: ").append(sub.getCounters()).append(" -> ").append(newCounters).append("; ");
                            sub.setCounters(newCounters);
                            hasChanges = true;
                        }

                        if (!newCheckinZone.equals(sub.getCheckin_zone())) {
                            changeLog.append("Check-in zone: ").append(sub.getCheckin_zone()).append(" -> ").append(newCheckinZone).append("; ");
                            sub.setCheckin_zone(newCheckinZone);
                            hasChanges = true;
                        }

                        if (!newStatus.equals(sub.getLast_status())) {
                            changeLog.append("Status: ").append(sub.getLast_status()).append(" -> ").append(newStatus).append("; ");
                            sub.setLast_status(newStatus);
                            hasChanges = true;
                        }

                        if (hasChanges) {
                            sub.setLast_updated(Instant.now().toString());
                            System.out.println("User " + user.getEmail() + " notified about flight " + sub.getFlight_number() + " changes: " + changeLog.toString());
                            
                            // Send email notification asynchronously if service is available
                            if (emailSenderService != null) {
                                emailSenderService.sendHtmlEmailAsync(
                                    user.getEmail(), 
                                    sub.getAirline_code(),
                                    sub.getFlight_number(), 
                                    changeLog.toString()
                                ).subscribe(
                                    null, // onNext (not needed for Mono<Void>)
                                    error -> System.err.println("Failed to send email to " + user.getEmail() + ": " + error.getMessage()),
                                    () -> System.out.println("Email notification sent successfully to " + user.getEmail())
                                );
                            } else {
                                System.out.println("Email service not available - notification logged only");
                            }
                            
                            updated = true;
                        }
                    }
                }

                if (updated) {
                    userRepository.save(user).subscribe();
                }
            });
        });
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
