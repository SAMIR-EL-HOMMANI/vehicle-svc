package com.demo.vehicle.api.service.impl;

import com.demo.vehicle.api.dto.AccessoryDto;
import com.demo.vehicle.api.exception.ResourceNotFoundException;
import com.demo.vehicle.api.mapper.AccessoryMapper;
import com.demo.vehicle.api.repository.AccessoryRepository;
import com.demo.vehicle.api.repository.VehicleRepository;
import com.demo.vehicle.api.service.AccessoryService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AccessoryServiceImpl implements AccessoryService {
    private final AccessoryRepository accessoryRepository;
    private final AccessoryMapper accessoryMapper;
    private final VehicleRepository vehicleRepository;

    public AccessoryServiceImpl(AccessoryRepository accessoryRepository, AccessoryMapper accessoryMapper, VehicleRepository vehicleRepository) {
        this.accessoryRepository = accessoryRepository;
        this.accessoryMapper = accessoryMapper;
        this.vehicleRepository = vehicleRepository;
    }

    @Override
    public List<AccessoryDto> findAll() {
        return accessoryMapper.toDtos(accessoryRepository.findAll());
    }

    @Override
    public List<AccessoryDto> findByVehicleId(Long vehicleId) {
        return accessoryMapper.toDtos(accessoryRepository.findByVehicleId(vehicleId));
    }


    @Override
    public Optional<AccessoryDto> findById(Long id) {
        return accessoryRepository.findById(id).map(accessoryMapper::toDto);
    }

    @Override
    public AccessoryDto save(AccessoryDto accessoryDto) {
        Long vehicleId = accessoryDto.vehicleId();
        if (vehicleId == null || !vehicleRepository.existsById(vehicleId)) {
            throw new ResourceNotFoundException("Vehicle with ID %s not found".formatted(vehicleId));
        }
        var entity = accessoryMapper.toEntity(accessoryDto);
        return accessoryMapper.toDto(accessoryRepository.save(entity));
    }

    @Override
    public void deleteById(Long id) {
        accessoryRepository.deleteById(id);
    }
}

