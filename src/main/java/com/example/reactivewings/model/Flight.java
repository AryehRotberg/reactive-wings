package com.example.reactivewings.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonAlias;

@Document(collection = "flights")
public class Flight
{
    @Id
    private String id;

    @JsonAlias("_id")
    private Long flightId;

    @JsonAlias("CHOPER")
    private String airlineCode;

    @JsonAlias("CHFLTN")
    private String flightNumber;

    @JsonAlias("CHOPERD")
    private String airlineName;

    @JsonAlias("CHSTOL")
    private String scheduledTime;

    @JsonAlias("CHPTOL")
    private String estimatedTime;

    @JsonAlias("CHAORD")
    private String direction;

    @JsonAlias("CHLOC1")
    private String airportCode;

    @JsonAlias("CHLOC1D")
    private String cityEn;

    @JsonAlias("CHLOC1TH")
    private String cityHe;

    @JsonAlias("CHLOC1T")
    private String cityName;

    @JsonAlias("CHLOCCT")
    private String countryEn;

    @JsonAlias("CHLOC1CH")
    private String countryHe;

    @JsonAlias("CHTERM")
    private int terminal;

    @JsonAlias("CHCINT")
    private String counters;

    @JsonAlias("CHCKZN")
    private String checkinZone;

    @JsonAlias("CHRMINE")
    private String statusEn;

    @JsonAlias("CHRMINH")
    private String statusHe;

    private LocalDateTime lastUpdated;

    // Constructors
    public Flight() {}

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getFlightId() { return flightId; }
    public void setFlightId(Long flightId) { this.flightId = flightId; }

    public String getAirlineCode() { return airlineCode; }
    public void setAirlineCode(String airlineCode) { this.airlineCode = airlineCode; }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }

    public String getAirlineName() { return airlineName; }
    public void setAirlineName(String airlineName) { this.airlineName = airlineName; }

    public String getScheduledTime() { return scheduledTime; }
    public void setScheduledTime(String scheduledTime) { this.scheduledTime = scheduledTime; }

    public String getEstimatedTime() { return estimatedTime; }
    public void setEstimatedTime(String estimatedTime) { this.estimatedTime = estimatedTime; }

    public String getDirection() { return direction; }
    public void setDirection(String direction) { this.direction = direction; }

    public String getAirportCode() { return airportCode; }
    public void setAirportCode(String airportCode) { this.airportCode = airportCode; }

    public String getCityEn() { return cityEn; }
    public void setCityEn(String cityEn) { this.cityEn = cityEn; }

    public String getCityHe() { return cityHe; }
    public void setCityHe(String cityHe) { this.cityHe = cityHe; }

    public String getCityName() { return cityName; }
    public void setCityName(String cityName) { this.cityName = cityName; }

    public String getCountryEn() { return countryEn; }
    public void setCountryEn(String countryEn) { this.countryEn = countryEn; }

    public String getCountryHe() { return countryHe; }
    public void setCountryHe(String countryHe) { this.countryHe = countryHe; }

    public int getTerminal() { return terminal; }
    public void setTerminal(int terminal) { this.terminal = terminal; }

    public String getCounters() { return counters; }
    public void setCounters(String counters) { this.counters = counters; }

    public String getCheckinZone() { return checkinZone; }
    public void setCheckinZone(String checkinZone) { this.checkinZone = checkinZone; }

    public String getStatusEn() { return statusEn; }
    public void setStatusEn(String statusEn) { this.statusEn = statusEn; }

    public String getStatusHe() { return statusHe; }
    public void setStatusHe(String statusHe) { this.statusHe = statusHe; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
