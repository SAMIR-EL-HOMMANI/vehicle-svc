package com.demo.vehicle.api.service;

import com.demo.vehicle.api.dto.GarageDto;
import com.demo.vehicle.api.dto.GarageSummaryDto;
import com.demo.vehicle.api.entity.Garage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import java.util.List;

public interface GarageService {
    List<Garage> findAll();
    Garage getByIdOrThrow(Long id);
    Garage getByIdOrThrowForUpdate(Long id);
    GarageDto findGarageByIdWithWorkingHours(Long id);
    GarageDto addGarage(GarageDto garage);
    GarageDto updateGarage(GarageDto garage);
    List<GarageSummaryDto> findGaragesByAccessoryName(String accessoryName);
    void deleteById(Long id);
    void checkGarageAvailability(Long garageId);
    Page<GarageDto> listGarages(int page, int size, List<Sort.Order> orders);

    }

