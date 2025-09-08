package com.example.flights.service;

import java.time.Instant;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.flights.model.FlightModel;
import com.example.flights.model.SubscriptionModel;
import com.example.flights.model.UserModel;
import com.example.flights.repo.FlightRepository;
import com.example.flights.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class SubscriptionService
{
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;

    public SubscriptionService(FlightRepository flightRepository, UserRepository userRepository, ObjectMapper objectMapper, EmailSenderService emailSenderService)
    {
        this.flightRepository = flightRepository;
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
    }

    @Scheduled(cron = "1/10 * * * * *")
    public void checkSubscriptions()
    {
        System.out.println("Synchronizing flight statuses...");

        flightRepository.findAll()
        .collectList()
        .doOnNext(flights -> System.out.println("Found " + flights.size() + " flights in database"))
        .flatMapMany(flights -> userRepository.findAll()
            .doOnNext(user -> System.out.println("Checking subscriptions for user: " + user.getEmail()))
            .flatMap(user -> processUserSubscriptions(user, flights)))
            .subscribeOn(Schedulers.boundedElastic())
        .subscribe(
            result -> {},
            error -> System.err.println("Error in subscription check: " + error.getMessage()),
            () -> System.out.println("Subscription check completed")
        );
    }

    private Mono<Void> processUserSubscriptions(UserModel user, List<FlightModel> flights)
    {
        boolean[] updated = { false };

        for (SubscriptionModel sub : user.getSubscriptions())
        {
            // Find matching flights for this subscription (there might be multiple due to direction differences)
            List<FlightModel> matchingFlights = flights.stream()
                .filter(flight -> isFlightMatchingSubscription(flight, sub))
                .toList();
            
            if (!matchingFlights.isEmpty()) {
                // If multiple flights match, prefer the one with the latest update time
                FlightModel flightData = matchingFlights.stream()
                    .max((f1, f2) -> {
                        if (f1.getLastUpdated() != null && f2.getLastUpdated() != null) {
                            return f1.getLastUpdated().compareTo(f2.getLastUpdated());
                        }
                        if (f1.getLastUpdated() != null) return 1;
                        if (f2.getLastUpdated() != null) return -1;
                        return 0;
                    })
                    .orElse(matchingFlights.get(0));
                
                if (applyChanges(flightData, sub, user)) {
                    updated[0] = true;
                }
            }
        }

        if (updated[0])
            return userRepository.save(user).then();
        return Mono.empty();
    }
    
    private boolean isFlightMatchingSubscription(FlightModel flight, SubscriptionModel sub)
    {
        return flight.getFlight_number() != null && flight.getFlight_number().equals(sub.getFlight_number()) &&
               flight.getAirline_code() != null && flight.getAirline_code().equals(sub.getAirline_code()) &&
               flight.getScheduled_time() != null && flight.getScheduled_time().equals(sub.getScheduled_time()) &&
               flight.getAirport_code() != null && flight.getAirport_code().equals(sub.getAirport_code());
    }

    private boolean applyChanges(FlightModel flightData, SubscriptionModel sub, UserModel user)
    {
        boolean hasChanges = false;
        StringBuilder changeLog = new StringBuilder();

        // Null-safe string comparison helper
        if (!safeEquals(flightData.getScheduled_time(), sub.getScheduled_time()))
        {
            changeLog.append("Scheduled time: ")
                    .append(sub.getScheduled_time())
                    .append(" &rarr; ")
                    .append(flightData.getScheduled_time())
                    .append("<br>");
            sub.setScheduled_time(flightData.getScheduled_time());
            hasChanges = true;
        }

        if (!safeEquals(flightData.getPlanned_time(), sub.getPlanned_time()))
        {
            changeLog.append("Planned time: ")
                    .append(sub.getPlanned_time())
                    .append(" &rarr; ")
                    .append(flightData.getPlanned_time())
                    .append("<br>");
            sub.setPlanned_time(flightData.getPlanned_time());
            hasChanges = true;
        }

        String flightTerminal = String.valueOf(flightData.getTerminal());
        if (!safeEquals(flightTerminal, sub.getTerminal()))
        {
            changeLog.append("Terminal: ")
                    .append(sub.getTerminal())
                    .append(" &rarr; ")
                    .append(flightTerminal)
                    .append("<br>");
            sub.setTerminal(flightTerminal);
            hasChanges = true;
        }

        if (!safeEquals(flightData.getCounters(), sub.getCounters()))
        {
            changeLog.append("Counters: ")
                    .append(sub.getCounters())
                    .append(" &rarr; ")
                    .append(flightData.getCounters())
                    .append("<br>");
            sub.setCounters(flightData.getCounters());
            hasChanges = true;
        }

        if (!safeEquals(flightData.getCheckin_zone(), sub.getCheckin_zone()))
        {
            changeLog.append("Check-in zone: ")
                    .append(sub.getCheckin_zone())
                    .append(" &rarr; ")
                    .append(flightData.getCheckin_zone())
                    .append("<br>");
            sub.setCheckin_zone(flightData.getCheckin_zone());
            hasChanges = true;
        }

        if (!safeEquals(flightData.getStatus_en(), sub.getLast_status()))
        {
            changeLog.append("Status: ")
                    .append(sub.getLast_status())
                    .append(" &rarr; ")
                    .append(flightData.getStatus_en())
                    .append("<br>");
            sub.setLast_status(flightData.getStatus_en());
            hasChanges = true;
        }

        if (hasChanges)
        {
            sub.setLast_updated(Instant.now().toString());
            System.out.println("User " + user.getEmail() + " notified about flight " + sub.getFlight_number() + " changes: " + changeLog.toString());
            
            if (emailSenderService != null)
            {
                emailSenderService.sendEmailAsync(
                    user.getEmail(), 
                    sub.getAirline_code(),
                    sub.getFlight_number(), 
                    changeLog.toString()
                ).subscribe(
                    null,
                    error -> System.err.println("Failed to send email to " + user.getEmail() + ": " + error.getMessage()),
                    () -> System.out.println("Email notification sent successfully to " + user.getEmail())
                );
            }
            
            else
                System.out.println("Email service not available - notification logged only");
        }
        
        return hasChanges;
    }
    
    private boolean safeEquals(String str1, String str2)
    {
        if (str1 == null && str2 == null) return true;
        if (str1 == null || str2 == null) return false;
        return str1.equals(str2);
    }
}
