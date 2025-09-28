package com.example.reactivewings.service;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.reactivewings.model.Flight;
import com.example.reactivewings.model.User;
import com.example.reactivewings.repo.UserRepository;
import com.example.reactivewings.utils.SubscriptionServiceUtils;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SubscriptionService {
    private static final Logger log = LoggerFactory.getLogger(SubscriptionService.class);

    private static UserRepository userRepository;
    private static EmailSenderService emailSenderService;
    private static ReactiveMongoTemplate mongoTemplate;

    public SubscriptionService(UserRepository userRepository, 
                                EmailSenderService emailSenderService,
                                ReactiveMongoTemplate mongoTemplate) {
        SubscriptionService.userRepository = userRepository;
        SubscriptionService.emailSenderService = emailSenderService;
        SubscriptionService.mongoTemplate = mongoTemplate;
    }

    private AtomicBoolean checkInProgress = new AtomicBoolean(false);

    @Scheduled(fixedDelay = 10000)
    public void checkSubscriptions() {
        if (!checkInProgress.compareAndSet(false, true)) {
            log.debug("Subscription check already running, skipping");
            return;
        }

        log.info("Checking subscriptions...");

        userRepository.findAll()
            .flatMap(this::processUserSubscriptions)
            .doOnError(error -> log.error("Error in subscription check: {}", error.getMessage()))
            .doFinally(signal -> {
                checkInProgress.set(false);
                log.info("Subscription check completed");
            })
            .subscribe();
    }

    private Mono<Void> processUserSubscriptions(User user) {
        return Flux.fromIterable(user.getSubscriptions())
            .concatMap(sub -> findMatchingFlight(sub)
                .map(flight -> applyChanges(flight, sub, user))
                .defaultIfEmpty(false)
            )
            .reduce(false, Boolean::logicalOr)
            .flatMap(changed -> changed ? userRepository.save(user).then() : Mono.empty());
    }
    
    private Mono<Flight> findMatchingFlight(Flight sub) {
        Query q = new Query();
        if (sub.getAirlineCode() != null) q.addCriteria(Criteria.where("airlineCode").is(sub.getAirlineCode()));
        if (sub.getFlightNumber() != null) q.addCriteria(Criteria.where("flightNumber").is(sub.getFlightNumber()));
        if (sub.getScheduledTime() != null) q.addCriteria(Criteria.where("scheduledTime").is(sub.getScheduledTime()));
        if (sub.getAirportCode() != null) q.addCriteria(Criteria.where("airportCode").is(sub.getAirportCode()));
        if (sub.getAirlineName() != null) q.addCriteria(Criteria.where("airlineName").is(sub.getAirlineName()));
        q.with(Sort.by(Sort.Direction.DESC, "lastUpdated"));
        q.limit(1);
        return mongoTemplate.findOne(q, Flight.class);
    }

    private boolean applyChanges(Flight matchingFlight, Flight sub, User user) {
        StringBuilder changeLog = new StringBuilder();

        boolean hasChanges = SubscriptionServiceUtils.updateField("Scheduled time", sub::getScheduledTime, matchingFlight::getScheduledTime, sub::setScheduledTime, changeLog) |
        SubscriptionServiceUtils.updateField("Estimated time", sub::getEstimatedTime, matchingFlight::getEstimatedTime, sub::setEstimatedTime, changeLog) |
        SubscriptionServiceUtils.updateField("Terminal", () -> String.valueOf(sub.getTerminal()), () -> String.valueOf(matchingFlight.getTerminal()), t -> sub.setTerminal(matchingFlight.getTerminal()), changeLog) |
        SubscriptionServiceUtils.updateField("Counters", sub::getCounters, matchingFlight::getCounters, sub::setCounters, changeLog) |
        SubscriptionServiceUtils.updateField("Check-in zone", sub::getCheckinZone, matchingFlight::getCheckinZone, sub::setCheckinZone, changeLog) |
        SubscriptionServiceUtils.updateField("Status", sub::getStatusEn, matchingFlight::getStatusEn, sub::setStatusEn, changeLog);

        if (hasChanges) {
            sub.setLastUpdated(LocalDateTime.now());
            if (emailSenderService != null) {
                emailSenderService.sendFlightUpdateEmailAsync(
                    user.getEmail(), 
                    sub.getAirlineCode(),
                    sub.getFlightNumber(), 
                    changeLog.toString()
                ).subscribe(null, err -> {}, () -> {});
            }
        }
        
        return hasChanges;
    }
}
