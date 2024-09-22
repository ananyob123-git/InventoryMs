package com.assignment.project.InventoryMs.repository;

import com.assignment.project.InventoryMs.model.Bus;
import com.assignment.project.InventoryMs.model.BusInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusInventoryRepo extends JpaRepository<BusInventory, Long> {


   public boolean existsByBusNumber(Long busNumber);
}
