package com.demo.vehicle.api.service.impl;

import com.demo.vehicle.api.dto.GarageDto;
import com.demo.vehicle.api.dto.GarageSummaryDto;
import com.demo.vehicle.api.entity.Garage;
import com.demo.vehicle.api.exception.ResourceNotFoundException;
import com.demo.vehicle.api.exception.ServiceException;
import com.demo.vehicle.api.mapper.GarageMapper;
import com.demo.vehicle.api.repository.GarageRepository;
import com.demo.vehicle.api.repository.VehicleRepository;
import com.demo.vehicle.api.service.GarageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
@Service
@Slf4j
public class GarageServiceImpl implements GarageService {

    private static final Set<String> ALLOWED_SORTS = Set.of("name", "address", "id");

    private final GarageRepository garageRepository;
    private final VehicleRepository vehicleRepository;
    private final GarageMapper garageMapper;

    public GarageServiceImpl(GarageRepository garageRepository, VehicleRepository vehicleRepository, GarageMapper garageMapper) {
        this.garageRepository = garageRepository;
        this.vehicleRepository = vehicleRepository;
        this.garageMapper = garageMapper;
    }

    public Page<GarageDto> listGarages(int page, int size, List<Sort.Order> orders) {
        List<Sort.Order> safeOrders = orders.stream()
                .filter(o -> ALLOWED_SORTS.contains(o.getProperty()))
                .toList();

        Sort sort = safeOrders.isEmpty() ? Sort.by("id").ascending() : Sort.by(safeOrders);
        Pageable pageable = PageRequest.of(page, size, sort);
        var pageOfEntities = garageRepository.findAll(pageable);
        return pageOfEntities.map(garageMapper::toDto);
    }

    @Override
    public List<Garage> findAll() {
        return garageRepository.findAll();
    }

    @Override
    public Garage getByIdOrThrow(Long id) {
        return garageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garage with ID %d not found".formatted(id)));
    }

    @Override
    @Transactional
    public Garage getByIdOrThrowForUpdate(Long id) {
        return garageRepository.findByIdForUpdate(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garage with ID %d not found".formatted(id)));
    }

    @Override
    public GarageDto findGarageByIdWithWorkingHours(Long id) {
        var garage = garageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Garage with ID %d not found".formatted(id)));
        return garageMapper.toDto(garage);
    }

    @Override
    @Transactional
    public GarageDto addGarage(GarageDto garageDto) {
        log.info("Adding new garage : {}", garageDto);
        var savedGarage = garageRepository.save(garageMapper.toEntity(garageDto));
        return garageMapper.toDto(savedGarage);
    }

    @Override
    @Transactional
    public GarageDto updateGarage(GarageDto garageDto) {
        log.info("Updating garage : {}", garageDto);
        if (!garageRepository.existsById(garageDto.id()))
            throw new ResourceNotFoundException("Garage with ID %d not found".formatted(garageDto.id()));
        var savedGarage = garageRepository.save(garageMapper.toEntity(garageDto));
        return garageMapper.toDto(savedGarage);
    }

    @Override
    public List<GarageSummaryDto> findGaragesByAccessoryName(String accessoryName) {
        log.info("Searching garages for vehicles having accessory with name: {}", accessoryName);
        return garageRepository.findGaragesByAccessoryName(accessoryName);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        garageRepository.deleteById(id);
    }

    public void checkGarageAvailability(Long garageId){
        if (vehicleRepository.countByGarageId(garageId) >= 50) {
            throw new ServiceException("Garage with Id %d reached maximum amount of vehicles".formatted(garageId));
        }
    }
}

