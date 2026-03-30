package com.demo.vehicle.api.repository;

import com.demo.vehicle.api.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	long countByGarageId(Long garageId);

	List<Vehicle> findByGarageId(Long garageId);

	List<Vehicle> findByModelIgnoreCaseAndGarageIdIn(String model, List<Long> garageIds);

}

