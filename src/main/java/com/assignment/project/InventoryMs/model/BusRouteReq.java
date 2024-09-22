package com.assignment.project.InventoryMs.model;

import lombok.Data;

@Data
public class BusRouteReq {
    private Long busNumber;
    private Integer availableSeats;
}
