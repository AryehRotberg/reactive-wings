package com.example.reactivewings.controller;

import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reactivewings.model.Flight;
import com.example.reactivewings.utils.FlightsControllerUtils;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/flights")
public class FlightsController {
    private final ReactiveMongoTemplate mongoTemplate;

    public FlightsController(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("/")
    public Flux<Flight> getFlights(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "100") int size) {
        Query query = FlightsControllerUtils.buildSearchQuery(page, size);
        return mongoTemplate.find(query, Flight.class);
    }

    @GetMapping("/search")
    public Flux<Flight> searchFlight(
        @RequestParam(required = false) String airlineCode,
        @RequestParam(required = false) String flightNumber,
        @RequestParam(required = false) String scheduledDate,
        @RequestParam(required = false) String estimatedDate,
        @RequestParam(required = false) String direction,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size
    ) {
        Query query = FlightsControllerUtils.buildSearchQuery(page, size);
        
        if (airlineCode != null) query.addCriteria(Criteria.where("airlineCode").is(airlineCode.toUpperCase()));
        if (flightNumber != null) query.addCriteria(Criteria.where("flightNumber").is(flightNumber));
        if (direction != null) query.addCriteria(Criteria.where("direction").is(direction.toUpperCase()));
        if (city != null) query.addCriteria(Criteria.where("cityName").is(city.toUpperCase()));
        if (status != null) query.addCriteria(Criteria.where("statusEn").is(status.toUpperCase()));

        if (scheduledDate != null)
            query.addCriteria(Criteria.where("scheduledTime").regex(java.util.regex.Pattern.quote(scheduledDate)));

        if (estimatedDate != null)
            query.addCriteria(Criteria.where("estimatedTime").regex(java.util.regex.Pattern.quote(estimatedDate)));

        return mongoTemplate.find(query, Flight.class);
    }
}
