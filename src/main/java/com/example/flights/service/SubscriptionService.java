package com.example.flights.service;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.flights.model.FlightModel;
import com.example.flights.model.SubscriptionModel;
import com.example.flights.model.UserModel;
import com.example.flights.repo.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Service
public class SubscriptionService
{
    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    private final UserRepository userRepository;
    private final EmailSenderService emailSenderService;
    private final ReactiveMongoTemplate mongoTemplate;

    private final AtomicBoolean checkInProgress = new AtomicBoolean(false);

    public SubscriptionService(UserRepository userRepository, ObjectMapper objectMapper, EmailSenderService emailSenderService, ReactiveMongoTemplate mongoTemplate)
    {
        this.userRepository = userRepository;
        this.emailSenderService = emailSenderService;
        this.mongoTemplate = mongoTemplate;
    }

    @Scheduled(fixedDelay = 10000)
    public void checkSubscriptions()
    {
        if (!checkInProgress.compareAndSet(false, true))
        {
            log.debug("Subscription check already running, skipping");
            return;
        }

        log.info("Checking subscriptions...");

        userRepository.findAll()
            .flatMap(user -> processUserSubscriptions(user).thenReturn(true))
            .subscribeOn(Schedulers.boundedElastic())
            .doOnError(error -> log.error("Error in subscription check: {}", error.getMessage()))
            .doFinally(signal ->
            {
                checkInProgress.set(false);
                log.info("Subscription check completed");
            })
            .subscribe();
    }

    private Mono<Void> processUserSubscriptions(UserModel user)
    {
        return Flux.fromIterable(user.getSubscriptions())
            .concatMap(sub -> findMatchingFlight(sub)
                .map(flight -> applyChanges(flight, sub, user))
                .defaultIfEmpty(false)
            )
            .reduce(false, (acc, changed) -> acc || changed)
            .flatMap(changed -> changed ? userRepository.save(user).then() : Mono.empty());
    }
    
    private Mono<FlightModel> findMatchingFlight(SubscriptionModel sub)
    {
        Query q = new Query();
        if (sub.getAirline_code() != null) q.addCriteria(Criteria.where("airline_code").is(sub.getAirline_code()));
        if (sub.getFlight_number() != null) q.addCriteria(Criteria.where("flight_number").is(sub.getFlight_number()));
        if (sub.getScheduled_time() != null) q.addCriteria(Criteria.where("scheduled_time").is(sub.getScheduled_time()));
        if (sub.getAirport_code() != null) q.addCriteria(Criteria.where("airport_code").is(sub.getAirport_code()));
        q.with(Sort.by(Sort.Direction.DESC, "lastUpdated"));
        q.limit(1);
        return mongoTemplate.findOne(q, FlightModel.class);
    }

    private boolean applyChanges(FlightModel flightData, SubscriptionModel sub, UserModel user)
    {
        boolean hasChanges = false;
        StringBuilder changeLog = new StringBuilder();

        if (!safeEquals(flightData.getScheduled_time(), sub.getScheduled_time()))
        {
            changeLog.append("Scheduled time: ")
                    .append(sub.getScheduled_time())
                    .append(" \u2192 ")
                    .append(flightData.getScheduled_time())
                    .append("<br>");
            sub.setScheduled_time(flightData.getScheduled_time());
            hasChanges = true;
        }

        if (!safeEquals(flightData.getPlanned_time(), sub.getPlanned_time()))
        {
            changeLog.append("Planned time: ")
                    .append(sub.getPlanned_time())
                    .append(" \u2192 ")
                    .append(flightData.getPlanned_time())
                    .append("<br>");
            sub.setPlanned_time(flightData.getPlanned_time());
            hasChanges = true;
        }

        String flightTerminal = (flightData.getTerminal() < 0) ? null : String.valueOf(flightData.getTerminal());
        if (!safeEquals(flightTerminal, sub.getTerminal()))
        {
            changeLog.append("Terminal: ")
                    .append(sub.getTerminal())
                    .append(" \u2192 ")
                    .append(flightTerminal)
                    .append("<br>");
            sub.setTerminal(flightTerminal);
            hasChanges = true;
        }

        if (!safeEquals(flightData.getCounters(), sub.getCounters()))
        {
            changeLog.append("Counters: ")
                    .append(sub.getCounters())
                    .append(" \u2192 ")
                    .append(flightData.getCounters())
                    .append("<br>");
            sub.setCounters(flightData.getCounters());
            hasChanges = true;
        }

        if (!safeEquals(flightData.getCheckin_zone(), sub.getCheckin_zone()))
        {
            changeLog.append("Check-in zone: ")
                    .append(sub.getCheckin_zone())
                    .append(" \u2192 ")
                    .append(flightData.getCheckin_zone())
                    .append("<br>");
            sub.setCheckin_zone(flightData.getCheckin_zone());
            hasChanges = true;
        }

        if (!safeEquals(flightData.getStatus_en(), sub.getLast_status()))
        {
            changeLog.append("Status: ")
                    .append(sub.getLast_status())
                    .append(" \u2192 ")
                    .append(flightData.getStatus_en())
                    .append("<br>");
            sub.setLast_status(flightData.getStatus_en());
            hasChanges = true;
        }

        if (hasChanges)
        {
            sub.setLast_updated(Instant.now().toString());
            if (emailSenderService != null)
            {
                emailSenderService.sendEmailAsync(
                    user.getEmail(), 
                    sub.getAirline_code(),
                    sub.getFlight_number(), 
                    changeLog.toString()
                ).subscribe(null, err -> {}, () -> {});
            }
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
