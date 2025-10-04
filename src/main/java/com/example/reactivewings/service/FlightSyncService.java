package com.example.reactivewings.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.example.reactivewings.model.Flight;
import com.example.reactivewings.repo.FlightRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class FlightSyncService {
    
    private static final Logger log = LoggerFactory.getLogger(FlightSyncService.class);

    private final FlightRepository flightRepository;
    private final BenGurionAPI bgnAPI;
    private final ObjectMapper objectMapper;
    private final AtomicBoolean syncInProgress = new AtomicBoolean(false);

    public FlightSyncService(FlightRepository flightRepository,
                             BenGurionAPI bgnAPI,
                             ObjectMapper objectMapper) {
        this.flightRepository = flightRepository;
        this.bgnAPI = bgnAPI;
        this.objectMapper = objectMapper;
    }
    
    @Value("${flight-sync.batch-size:50}")
    private int batchSize;
    
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
    
    private Mono<Long> fetchAndReplaceFlights() {
        LocalDateTime syncTime = LocalDateTime.now();
        
        return bgnAPI.getBenGurionFlightRecords()
            .map(record -> {
                Flight flight = objectMapper.convertValue(record, Flight.class);
                flight.setLastUpdated(syncTime);
                return flight;
            })
            .buffer(batchSize)
            .flatMap(batch -> flightRepository.saveAll(batch).count())
            .reduce(0L, Long::sum)
            .flatMap(savedCount -> 
                flightRepository.deleteByLastUpdatedBefore(syncTime)
                    .doOnSuccess(deleted -> log.debug("Removed {} outdated records", deleted))
                    .thenReturn(savedCount)
            );
    }
}
