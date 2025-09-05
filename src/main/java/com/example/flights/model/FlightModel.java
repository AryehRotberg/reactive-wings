package com.example.flights.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonAlias;

@Document(collection = "flights")
public class FlightModel
{
    @Id
    private String id;

    @JsonAlias("_id")
    private Long externalId;

    @JsonAlias("CHOPER")
    private String airline_code;

    @JsonAlias("CHFLTN")
    private String flight_number;

    @JsonAlias("CHOPERD")
    private String airline_name;

    @JsonAlias("CHSTOL")
    private String scheduled_time;

    @JsonAlias("CHPTOL")
    private String planned_time;

    @JsonAlias("CHAORD")
    private String direction;

    @JsonAlias("CHLOC1")
    private String airport_code;

    @JsonAlias("CHLOC1D")
    private String city_en;

    @JsonAlias("CHLOC1TH")
    private String city_he;

    @JsonAlias("CHLOC1T")
    private String city_name;

    @JsonAlias("CHLOCCT")
    private String country_en;

    @JsonAlias("CHLOC1CH")
    private String country_he;

    @JsonAlias("CHTERM")
    private int terminal;

    @JsonAlias("CHCINT")
    private String counters;

    @JsonAlias("CHCKZN")
    private String checkin_zone;

    @JsonAlias("CHRMINE")
    private String status_en;

    @JsonAlias("CHRMINH")
    private String status_he;

    // Constructors
    public FlightModel() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getExternalId() { return externalId; }
    public void setExternalId(Long externalId) { this.externalId = externalId; }

    public String getAirline_code() { return airline_code; }
    public void setAirline_code(String airline_code) { this.airline_code = airline_code; }

    public String getFlight_number() { return flight_number; }
    public void setFlight_number(String flight_number) { this.flight_number = flight_number; }

    public String getAirline_name() { return airline_name; }
    public void setAirline_name(String airline_name) { this.airline_name = airline_name; }

    public String getScheduled_time() { return scheduled_time; }
    public void setScheduled_time(String scheduled_time) { this.scheduled_time = scheduled_time; }

    public String getPlanned_time() { return planned_time; }
    public void setPlanned_time(String planned_time) { this.planned_time = planned_time; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getAirport_code() { return airport_code; }
    public void setAirport_code(String airport_code) { this.airport_code = airport_code; }

    public String getCity_en() { return city_en; }
    public void setCity_en(String city_en) { this.city_en = city_en; }

    public String getCity_he() { return city_he; }
    public void setCity_he(String city_he) { this.city_he = city_he; }

    public String getCity_name() { return city_name; }
    public void setCity_name(String city_name) { this.city_name = city_name; }

    public String getCountry_en() { return country_en; }
    public void setCountry_en(String country_en) { this.country_en = country_en; }

    public String getCountry_he() { return country_he; }
    public void setCountry_he(String country_he) { this.country_he = country_he; }

    public int getTerminal() { return terminal; }
    public void setTerminal(int terminal) { this.terminal = terminal; }

    public String getCounters() { return counters; }
    public void setCounters(String counters) { this.counters = counters; }

    public String getCheckin_zone() { return checkin_zone; }
    public void setCheckin_zone(String checkin_zone) { this.checkin_zone = checkin_zone; }

    public String getStatus_en() { return status_en; }
    public void setStatus_en(String status_en) { this.status_en = status_en; }

    public String getStatus_he() { return status_he; }
    public void setStatus_he(String status_he) { this.status_he = status_he; }
}
