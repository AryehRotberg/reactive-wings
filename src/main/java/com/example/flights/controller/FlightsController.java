package com.example.flights.controller;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.flights.model.FlightModel;
import reactor.core.publisher.Flux;

@RestController
public class FlightsController
{
    private final ReactiveMongoTemplate mongoTemplate;

    public FlightsController(ReactiveMongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    @GetMapping("flights")
    public Flux<FlightModel> getFlights(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "100") int size)
    {
        int limit = Math.min(size, 500);
        int skip = Math.max(page, 0) * limit;

        Query query = new Query()
            .skip(skip)
            .limit(limit)
            .with(Sort.by(Sort.Direction.DESC, "lastUpdated"));

        return mongoTemplate.find(query, FlightModel.class);
    }

    @GetMapping("flights/search")
    public Flux<FlightModel> searchFlight(
        @RequestParam(required = false) String airline_code,
        @RequestParam(required = false) String flight_number,
        @RequestParam(required = false) String scheduled_date,
        @RequestParam(required = false) String estimated_date,
        @RequestParam(required = false) String direction,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size
    )
    {
        Query query = new Query();
        if (airline_code != null) query.addCriteria(Criteria.where("airline_code").is(airline_code.toUpperCase()));
        if (flight_number != null) query.addCriteria(Criteria.where("flight_number").is(flight_number));
        if (direction != null) query.addCriteria(Criteria.where("direction").is(direction.toUpperCase()));
        if (city != null) query.addCriteria(Criteria.where("city_name").is(city.toUpperCase()));
        if (status != null) query.addCriteria(Criteria.where("status_en").is(status.toUpperCase()));

        if (scheduled_date != null)
            query.addCriteria(Criteria.where("scheduled_time").regex("^" + java.util.regex.Pattern.quote(scheduled_date)));

        if (estimated_date != null)
            query.addCriteria(Criteria.where("estimated_time").regex("^" + java.util.regex.Pattern.quote(estimated_date)));

        int limit = Math.min(size, 500);
        int skip = Math.max(page, 0) * limit;
        query.skip(skip).limit(limit);
        query.with(Sort.by(Sort.Direction.DESC, "lastUpdated"));
        return mongoTemplate.find(query, FlightModel.class);
    }
}
