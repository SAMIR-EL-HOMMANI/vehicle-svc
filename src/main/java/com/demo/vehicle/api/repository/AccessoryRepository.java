package com.demo.vehicle.api.repository;

import com.demo.vehicle.api.entity.Accessory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AccessoryRepository extends JpaRepository<Accessory, Long> {
	List<Accessory> findByVehicleId(Long vehicleId);
}

