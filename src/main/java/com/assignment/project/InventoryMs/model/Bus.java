package com.assignment.project.InventoryMs.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Bus {

    @JsonProperty("bookingNumber")
    private String bookingNumber;

    @JsonProperty("numberOfSeats")
    private Integer numberOfSeats;

    public Bus(){}

    public Bus(String bookingNumber, Integer numberOfSeats) {
        this.bookingNumber=bookingNumber;
        this.numberOfSeats=numberOfSeats;
    }
}
