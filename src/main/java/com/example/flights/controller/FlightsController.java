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
        @RequestParam(required = false) String scheduled_time,
        @RequestParam(required = false) String planned_date,
        @RequestParam(required = false) String planned_time,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "100") int size
    )
    {
        Query query = new Query();
        if (airline_code != null) query.addCriteria(Criteria.where("airline_code").is(airline_code));
        if (flight_number != null) query.addCriteria(Criteria.where("flight_number").is(flight_number));

        if (scheduled_date != null)
            query.addCriteria(Criteria.where("scheduled_time").regex("^" + java.util.regex.Pattern.quote(scheduled_date)));

        if (scheduled_time != null)
        {
            if (scheduled_time.matches("\\d{4}-\\d{2}-\\d{2}"))
                query.addCriteria(Criteria.where("scheduled_time").regex("^" + java.util.regex.Pattern.quote(scheduled_time)));
            else
                query.addCriteria(Criteria.where("scheduled_time").regex(java.util.regex.Pattern.quote(scheduled_time) + "$"));
        }

        if (planned_date != null)
            query.addCriteria(Criteria.where("planned_time").regex("^" + java.util.regex.Pattern.quote(planned_date)));
        if (planned_time != null)
            query.addCriteria(Criteria.where("planned_time").regex(java.util.regex.Pattern.quote(planned_time) + "$"));

        int limit = Math.min(size, 500);
        int skip = Math.max(page, 0) * limit;
        query.skip(skip).limit(limit);
        query.with(Sort.by(Sort.Direction.DESC, "lastUpdated"));
        return mongoTemplate.find(query, FlightModel.class);
    }
}
