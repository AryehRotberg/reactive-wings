package com.example.flights.service;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        System.out.println("Synchronizing flight statuses.");

        flightRepository.findAll()
        .collectList()
        .flatMapMany(flights -> userRepository.findAll()
            .flatMap(user -> processUserSubscriptions(user, flights)))
            .subscribeOn(Schedulers.boundedElastic())
        .subscribe();
    }

    private Mono<Void> processUserSubscriptions(UserModel user, List<FlightModel> flights)
    {
        Map<String, FlightModel> flightMap = flights.stream()
            .collect(Collectors.toMap(this::buildKey, f -> f));

        boolean[] updated = { false };

        for (SubscriptionModel sub : user.getSubscriptions())
        {                    
            FlightModel flightData = flightMap.get(buildKey(sub));

            if (flightData != null && applyChanges(flightData, sub, user))
                updated[0] = true;
        }

        if (updated[0])
            return userRepository.save(user).then();
        return Mono.empty();
    }

    private String buildKey(FlightModel flightModel)
    {
        return flightModel.getFlight_number() + "-" +
                flightModel.getAirline_code() + "-" +
                flightModel.getScheduled_time() + "-" +
                flightModel.getAirport_code();
    }

    private String buildKey(SubscriptionModel sub)
    {
        return sub.getFlight_number() + "-" +
                sub.getAirline_code() + "-" +
                sub.getScheduled_time() + "-" +
                sub.getAirport_code();
    }

    private boolean applyChanges(FlightModel flightData, SubscriptionModel sub, UserModel user)
    {
        boolean hasChanges = false;
        StringBuilder changeLog = new StringBuilder();

        if (!flightData.getScheduled_time().equals(sub.getScheduled_time()))
        {
            changeLog.append("Scheduled time: ")
                    .append(sub.getScheduled_time())
                    .append(" &rarr; ")
                    .append(flightData.getScheduled_time())
                    .append("<br>");
            sub.setScheduled_time(flightData.getScheduled_time());
            hasChanges = true;
        }

        if (!flightData.getPlanned_time().equals(sub.getPlanned_time()))
        {
            changeLog.append("Planned time: ")
                    .append(sub.getPlanned_time())
                    .append(" &rarr; ")
                    .append(flightData.getPlanned_time())
                    .append("<br>");
            sub.setPlanned_time(flightData.getPlanned_time());
            hasChanges = true;
        }

        if (!String.valueOf(flightData.getTerminal()).equals(sub.getTerminal()))
        {
            changeLog.append("Terminal: ")
                    .append(sub.getTerminal())
                    .append(" &rarr; ")
                    .append(flightData.getTerminal())
                    .append("<br>");
            sub.setTerminal(String.valueOf(flightData.getTerminal()));
            hasChanges = true;
        }

        if (!flightData.getCounters().equals(sub.getCounters()))
        {
            changeLog.append("Counters: ")
                    .append(sub.getCounters())
                    .append(" &rarr; ")
                    .append(flightData.getCounters())
                    .append("<br>");
            sub.setCounters(flightData.getCounters());
            hasChanges = true;
        }

        if (!flightData.getCheckin_zone().equals(sub.getCheckin_zone()))
        {
            changeLog.append("Check-in zone: ")
                    .append(sub.getCheckin_zone())
                    .append(" &rarr; ")
                    .append(flightData.getCheckin_zone())
                    .append("<br>");
            sub.setCheckin_zone(flightData.getCheckin_zone());
            hasChanges = true;
        }

        if (!flightData.getStatus_en().equals(sub.getLast_status()))
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
}
