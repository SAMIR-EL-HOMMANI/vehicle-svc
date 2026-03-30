package com.demo.vehicle.api.service;

import com.demo.vehicle.api.dto.AccessoryDto;
import java.util.List;
import java.util.Optional;

public interface AccessoryService {
    List<AccessoryDto> findAll();
    List<AccessoryDto> findByVehicleId(Long vehicleId);
    Optional<AccessoryDto> findById(Long id);
    AccessoryDto save(AccessoryDto accessory);
    void deleteById(Long id);
}

