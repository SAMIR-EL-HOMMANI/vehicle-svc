package com.demo.vehicle.api.service.impl;

import com.demo.vehicle.api.dto.VehicleCreateDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.dto.VehicleUpdateDto;
import com.demo.vehicle.api.event.VehicleEventPublisher;
import com.demo.vehicle.api.exception.ResourceNotFoundException;
import com.demo.vehicle.api.mapper.VehicleMapper;
import com.demo.vehicle.api.repository.VehicleRepository;
import com.demo.vehicle.api.service.GarageService;
import com.demo.vehicle.api.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@Slf4j
public class VehicleServiceImpl implements VehicleService {
    private final VehicleRepository vehicleRepository;
    private final GarageService garageService;
    private final VehicleMapper vehicleMapper;
    private final VehicleEventPublisher eventPublisher;

    public VehicleServiceImpl(VehicleRepository vehicleRepository, GarageService garageService, VehicleMapper vehicleMapper, VehicleEventPublisher eventPublisher) {
        this.vehicleRepository = vehicleRepository;
        this.garageService = garageService;
        this.vehicleMapper = vehicleMapper;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public List<VehicleDto> findAll() {
        log.debug("Get all vehicles");
        return vehicleMapper.toDtos(vehicleRepository.findAll());
    }

    @Override
    public List<VehicleDto> findByGarageId(Long garageId) {
        garageService.getByIdOrThrow(garageId);
        return vehicleMapper.toDtos(vehicleRepository.findByGarageId(garageId));
    }

    @Override
    public List<VehicleDto> findByModelAndGarageIds(String model, List<Long> garageIds) {
        log.debug("Get vehicles by model {} and garageIds {}", model, garageIds);
        return vehicleMapper.toDtos(vehicleRepository.findByModelIgnoreCaseAndGarageIdIn(model, garageIds));
    }

    @Override
    public VehicleDto getByIdOrThrow(Long id) {
        return vehicleRepository.findById(id)
                .map(vehicleMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID %d not found".formatted(id)));
    }

    @Override
    @Transactional
    public VehicleDto createVehicle(VehicleCreateDto vehicle) {
        log.info("Create new vehicle {}", vehicle);
        var garage = garageService.getByIdOrThrowForUpdate(vehicle.garageId());
        garageService.checkGarageAvailability(vehicle.garageId());
        var vehicleEntity = vehicleMapper.createDtoToEntity(vehicle);
        vehicleEntity.setGarage(garage);
        var savedVehicle = vehicleRepository.save(vehicleEntity);
        var vehicleDto = vehicleMapper.toDto(savedVehicle);
        eventPublisher.publishVehicleCreatedEvent(vehicleDto);

        return vehicleDto;
    }

    @Override
    @Transactional
    public VehicleDto updateVehicle(VehicleUpdateDto vehicle) {
        log.info("Updating vehicle {}", vehicle);
        var vehicleBeforeUpdate = vehicleRepository.findById(vehicle.id())
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle with ID %d not found".formatted(vehicle.id())));
        var garageChanged = !Objects.equals(vehicleBeforeUpdate.getGarage().getId(), vehicle.garageId());
        var garage = garageChanged
                ? garageService.getByIdOrThrowForUpdate(vehicle.garageId())
                : garageService.getByIdOrThrow(vehicle.garageId());
        // Check garage availability in case of vehicle garage changed
        if (garageChanged) {
            garageService.checkGarageAvailability(vehicle.garageId());
        }
        var vehicleEntity = vehicleMapper.updateDtoToEntity(vehicle);
        vehicleEntity.setGarage(garage);
        var savedVehicle = vehicleRepository.save(vehicleEntity);
        return vehicleMapper.toDto(savedVehicle);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        vehicleRepository.deleteById(id);
    }
}

