package com.globetrotter.travel.model;

import java.time.LocalDate;

public class FlightSearchRequest {
    private String from;
    private String to;
    private String fromAirportCode;
    private String toAirportCode;
    private LocalDate fromDate;
    private LocalDate toDate;
    private int numberOfPassengers;
    private String travelClass;

    // Getters and setters
    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFromAirportCode() {
        return fromAirportCode;
    }

    public void setFromAirportCode(String fromAirportCode) {
        this.fromAirportCode = fromAirportCode;
    }

    public String getToAirportCode() {
        return toAirportCode;
    }

    public void setToAirportCode(String toAirportCode) {
        this.toAirportCode = toAirportCode;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }

    public int getNumberOfPassengers() {
        return numberOfPassengers;
    }

    public void setNumberOfPassengers(int numberOfPassengers) {
        this.numberOfPassengers = numberOfPassengers;
    }

    public String getTravelClass() {
        return travelClass;
    }

    public void setTravelClass(String travelClass) {
        this.travelClass = travelClass;
    }

    @Override
    public String toString() {
        return "FlightSearchRequest{" +
                "from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", fromAirportCode='" + fromAirportCode + '\'' +
                ", toAirportCode='" + toAirportCode + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", numberOfPassengers=" + numberOfPassengers +
                ", travelClass='" + travelClass + '\'' +
                '}';
    }
}
