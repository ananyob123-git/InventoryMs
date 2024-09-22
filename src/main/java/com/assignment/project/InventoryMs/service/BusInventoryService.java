package com.assignment.project.InventoryMs.service;

import com.assignment.project.InventoryMs.exception.InventoryException;
import com.assignment.project.InventoryMs.model.Bus;
import com.assignment.project.InventoryMs.model.BusInventory;
import com.assignment.project.InventoryMs.repository.BusInventoryRepo;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class BusInventoryService {

    private static final Logger logger = LoggerFactory.getLogger(BusInventoryService.class);

    @Autowired
    private BusInventoryRepo busInventoryRepo;

    @Autowired
    private WebClient webClient;

    private static final String TOPIC_BOOK_CONFIRMATION = "booking-confirmation";

    private static final String TOPIC_INVENTORY_FAILURE = "inventory-failure-topic";

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaTemplate<String, Bus> kafkaTemplatePayFail;

    public Integer getAvailableSeats(Long busNumber) {
        if (busInventoryRepo.findById(busNumber).isPresent()) {
            return busInventoryRepo.findById(busNumber).get().getAvailableSeats();
        }
        return null;
    }

    public List<BusInventory> addBusInventoryDetails(List<BusInventory> reqList) {
        List<BusInventory> inventoryList = new ArrayList<BusInventory>(reqList);

        return busInventoryRepo.saveAll(inventoryList);
    }

    @KafkaListener(topics = "payment_inventory_topic", groupId = "inventory-consumer-group")
    @Transactional
    public void consumePaymentEvent(Bus bus) {
        Long busNumber = 0l;
        if (bus != null) {
            logger.info("Inside BusInventoryService.consumePaymentEvent with Bus details: {}", bus);
            busNumber = getBusNumberFromBooking(bus.getBookingNumber());
        } else {
            logger.error("Bus details is null in BusInventoryService.consumePaymentEvent");
            throw new InventoryException("Bus details is coming as null from payment service listener");
        }
        BusInventory inventory = busInventoryRepo.findById(busNumber)
                .orElseThrow(() -> new RuntimeException("Bus not found!"));
        logger.info("inventory obtained: {}", inventory);
        Integer seatsAvailable = getAvailableSeats(busNumber);
        logger.info("seatsAvailable and seats requested obtained: {},{}", seatsAvailable, bus.getNumberOfSeats());
        if (seatsAvailable >= bus.getNumberOfSeats()) {
            inventory.setAvailableSeats(seatsAvailable - bus.getNumberOfSeats());
            inventory.setLastUpdatedDate(String.valueOf(new Date()));
            busInventoryRepo.save(inventory);
            kafkaTemplate.send(TOPIC_BOOK_CONFIRMATION, bus.getBookingNumber());
        } else {
            kafkaTemplatePayFail.send(TOPIC_INVENTORY_FAILURE, bus);
            throw new InventoryException("Not enough available seats so booking cannot be processed");
        }
    }

    private Long getBusNumberFromBooking(String bookingNumber) {
        try {
            return webClient
                    .get()
                    .uri("/bookingApi/getBusNumber/" + bookingNumber)
                    .retrieve()
                    .bodyToMono(Long.class)
                    .block();
        }catch(Exception ex){
            logger.error("Exception occurred while retrieving data from Booking service {}",ex.getMessage());
            throw new InventoryException("Exception occurred while retrieving data from Booking service"+ex.getMessage());
        }
    }
}