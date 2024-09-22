package com.assignment.project.InventoryMs.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Entity
@Data
public class BusInventory {

    @Id
    private Long busNumber;
    private int availableSeats;
    private String lastUpdatedDate;
}
