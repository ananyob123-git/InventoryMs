package com.assignment.project.InventoryMs.controller;

import com.assignment.project.InventoryMs.model.BusInventory;
import com.assignment.project.InventoryMs.service.BusInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("inventoryApi")
@Slf4j
public class InventoryController {

    Logger logger = LoggerFactory.getLogger(InventoryController.class);
    @Autowired
    private BusInventoryService service;

    @GetMapping("/getAvailableSeats/{busNumber}")
    public ResponseEntity<Integer> getAvailableSeats(@PathVariable Long busNumber){
        logger.info("Inside InventoryController.getAvailableSeats with busNumber parameter : "+busNumber);
        if(service.getAvailableSeats(busNumber)!=null){
            return ResponseEntity.ok(service.getAvailableSeats(busNumber));
        }
        return null;
    }

    @PostMapping("/addInventory")
    public void addBusInventoryDetails(@RequestBody List<BusInventory> reqList){
        logger.info("Inside InventoryController.addBusInventoryDetails");
        service.addBusInventoryDetails(reqList);
    }
}
