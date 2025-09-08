package com.example.flights.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import com.example.flights.model.FlightModel;
import com.example.flights.repo.FlightRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class FlightSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(FlightSyncService.class);
    
    private final FlightRepository flightRepository;
    private final BenGurionAPI bgnAPI;
    private final ObjectMapper objectMapper;
    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);
    
    @Value("${flight-sync.batch-size:50}")
    private int batchSize;
    
    public FlightSyncService(FlightRepository flightRepository, 
                            BenGurionAPI bgnAPI, 
                            ObjectMapper objectMapper) {
        this.flightRepository = flightRepository;
        this.bgnAPI = bgnAPI;
        this.objectMapper = objectMapper;
    }
    
    /**
     * Sync flights every minute (adjust as needed)
     */
    @Scheduled(fixedDelay = 60000)
    public void syncFlightsFromAPI() {
        if (!syncInProgress.compareAndSet(false, true)) {
            log.debug("Sync already in progress, skipping");
            return;
        }
        
        log.info("Starting flight sync");
        
        fetchAndReplaceFlights()
            .doOnSuccess(count -> log.info("Successfully synced {} flights", count))
            .doOnError(error -> log.error("Sync failed: {}", error.getMessage()))
            .doFinally(signal -> syncInProgress.set(false))
            .subscribe();
    }
    
    /**
     * Simple approach: Mark old, save new, delete old
     */
    private Mono<Long> fetchAndReplaceFlights() {
        LocalDateTime syncTime = LocalDateTime.now();
        
        return bgnAPI.getBenGurionFlights()
            .timeout(Duration.ofSeconds(30))
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
            .map(data -> data.path("result").path("records"))
            .flatMapMany(records -> Flux.fromIterable(records))
            .map(record -> {
                FlightModel flight = objectMapper.convertValue(record, FlightModel.class);
                flight.setLastUpdated(syncTime);
                return flight;
            })
            .buffer(batchSize)
            .flatMap(batch -> flightRepository.saveAll(batch).collectList())
            .count()
            .flatMap(batchCount -> {
                // Delete records that weren't updated in this sync
                return flightRepository.deleteByLastUpdatedBefore(syncTime)
                    .doOnSuccess(deleted -> log.debug("Removed {} outdated records", deleted))
                    .thenReturn(batchCount * batchSize);
            });
    }
    
    /**
     * Alternative: Complete replacement (simpler but brief downtime)
     */
    private Mono<Long> fetchAndReplaceAllFlights() {
        return bgnAPI.getBenGurionFlights()
            .timeout(Duration.ofSeconds(30))
            .retryWhen(Retry.backoff(3, Duration.ofSeconds(2)))
            .map(data -> data.path("result").path("records"))
            .flatMapMany(records -> Flux.fromIterable(records))
            .map(record -> objectMapper.convertValue(record, FlightModel.class))
            .collectList()
            .flatMap(flights -> {
                if (flights.isEmpty()) {
                    log.warn("No flights received from API");
                    return Mono.just(0L);
                }
                
                // Delete all then insert new (simple but has brief downtime)
                return flightRepository.deleteAll()
                    .then(flightRepository.saveAll(flights).count());
            });
    }
}
