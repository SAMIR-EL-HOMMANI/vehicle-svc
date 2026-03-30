package com.demo.vehicle.api.service;

import com.demo.vehicle.api.dto.VehicleCreateDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.dto.VehicleUpdateDto;
import java.util.List;

public interface VehicleService {
    List<VehicleDto> findAll();
    List<VehicleDto> findByGarageId(Long garageId);
    List<VehicleDto> findByModelAndGarageIds(String model, List<Long> garageIds);
    VehicleDto getByIdOrThrow(Long id);
    VehicleDto createVehicle(VehicleCreateDto vehicle);
    VehicleDto updateVehicle(VehicleUpdateDto vehicle);
    void deleteById(Long id);
}

