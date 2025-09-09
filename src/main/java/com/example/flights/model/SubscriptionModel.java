package com.example.flights.model;

public class SubscriptionModel
{
    private String flight_number;
    private String airline_code;
    private String scheduled_time;
    private String estimated_time;
    private String last_updated;
    private String airline_name;
    private String airport_code;
    private String city_en;
    private String country_en;
    private String terminal;
    private String counters;
    private String checkin_zone;
    private String last_status;

    public SubscriptionModel() {}

    public String getAirline_code() { return airline_code; }
    public void setAirline_code(String airline_code) { this.airline_code = airline_code; }

    public String getFlight_number() { return flight_number; }
    public void setFlight_number(String flight_number) { this.flight_number = flight_number; }

    public String getScheduled_time() { return scheduled_time; }
    public void setScheduled_time(String scheduled_time) { this.scheduled_time = scheduled_time; }

    public String getestimated_time() { return estimated_time; }
    public void setestimated_time(String estimated_time) { this.estimated_time = estimated_time; }

    public String getAirport_code() { return airport_code; }
    public void setAirport_code(String airport_code) { this.airport_code = airport_code; }

    public String getCity_en() { return city_en; }
    public void setCity_en(String city_en) { this.city_en = city_en; }

    public String getCountry_en() { return country_en; }
    public void setCountry_en(String country_en) { this.country_en = country_en; }

    public String getTerminal() { return terminal; }
    public void setTerminal(String terminal) { this.terminal = terminal; }

    public String getCounters() { return counters; }
    public void setCounters(String counters) { this.counters = counters; }

    public String getCheckin_zone() { return checkin_zone; }
    public void setCheckin_zone(String checkin_zone) { this.checkin_zone = checkin_zone; }

    public String getAirline_name() { return airline_name; }
    public void setAirline_name(String airline_name) { this.airline_name = airline_name; }

    public String getLast_updated() { return last_updated; }
    public void setLast_updated(String last_updated) { this.last_updated = last_updated; }
    
    public String getLast_status() { return last_status; }
    public void setLast_status(String last_status) { this.last_status = last_status; }

    @Override
    public String toString() {
        return "SubscriptionModel{" +
                "airline_code='" + airline_code + '\'' +
                ", flight_number='" + flight_number + '\'' +
                ", scheduled_time='" + scheduled_time + '\'' +
                ", estimated_time='" + estimated_time + '\'' +
                ", last_updated='" + last_updated + '\'' +
                ", airport_code='" + airport_code + '\'' +
                ", airline_name='" + airline_name + '\'' +
                ", city_en='" + city_en + '\'' +
                ", country_en='" + country_en + '\'' +
                ", terminal='" + terminal + '\'' +
                ", counters='" + counters + '\'' +
                ", checkin_zone='" + checkin_zone + '\'' +
                ", last_status='" + last_status + '\'' +
                '}';
    }
}
