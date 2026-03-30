package com.demo.vehicle.api.service;

import com.demo.vehicle.api.dto.AccessoryDto;
import com.demo.vehicle.api.entity.Accessory;
import com.demo.vehicle.api.exception.ResourceNotFoundException;
import com.demo.vehicle.api.mapper.AccessoryMapper;
import com.demo.vehicle.api.repository.AccessoryRepository;
import com.demo.vehicle.api.repository.VehicleRepository;
import com.demo.vehicle.api.service.impl.AccessoryServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccessoryServiceTest {

    @Mock
    AccessoryRepository accessoryRepository;
    @Mock
    AccessoryMapper accessoryMapper;
    @Mock
    VehicleRepository vehicleRepository;

    @InjectMocks
    AccessoryServiceImpl accessoryService;

    @Test
    void should_find_all_accessories() {
        Accessory entity = new Accessory();
        entity.setId(1L);
        AccessoryDto dto = new AccessoryDto(1L, "GPS", "Systeme navigation", 1500L, "Electronique", 10L);

        when(accessoryRepository.findAll()).thenReturn(List.of(entity));
        when(accessoryMapper.toDtos(List.of(entity))).thenReturn(List.of(dto));

        List<AccessoryDto> result = accessoryService.findAll();

        assertEquals(1, result.size());
        assertEquals("GPS", result.get(0).name());
        verify(accessoryRepository).findAll();
    }

    @Test
    void should_find_accessories_by_vehicle_id() {
        Accessory entity = new Accessory();
        entity.setId(1L);
        AccessoryDto dto = new AccessoryDto(1L, "GPS", "Systeme navigation", 1500L, "Electronique", 10L);

        when(accessoryRepository.findByVehicleId(10L)).thenReturn(List.of(entity));
        when(accessoryMapper.toDtos(List.of(entity))).thenReturn(List.of(dto));

        List<AccessoryDto> result = accessoryService.findByVehicleId(10L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).vehicleId());
        verify(accessoryRepository).findByVehicleId(10L);
    }


    @Test
    void should_find_accessory_by_id() {
        Accessory entity = new Accessory();
        entity.setId(1L);
        AccessoryDto dto = new AccessoryDto(1L, "GPS", "Systeme navigation", 1500L, "Electronique", 10L);

        when(accessoryRepository.findById(1L)).thenReturn(Optional.of(entity));
        when(accessoryMapper.toDto(entity)).thenReturn(dto);

        Optional<AccessoryDto> result = accessoryService.findById(1L);

        assertTrue(result.isPresent());
        assertEquals(dto, result.get());
    }

    @Test
    void should_return_empty_when_accessory_not_found() {
        when(accessoryRepository.findById(999L)).thenReturn(Optional.empty());

        Optional<AccessoryDto> result = accessoryService.findById(999L);

        assertTrue(result.isEmpty());
        verify(accessoryMapper, never()).toDto(any());
    }

    @Test
    void should_save_accessory() {
        AccessoryDto input = new AccessoryDto(null, "GPS", "Systeme navigation", 1500L, "Electronique", 10L);
        Accessory entity = new Accessory();
        Accessory saved = new Accessory();
        saved.setId(1L);
        AccessoryDto output = new AccessoryDto(1L, "GPS", "Systeme navigation", 1500L, "Electronique", 10L);

        when(vehicleRepository.existsById(10L)).thenReturn(true);
        when(accessoryMapper.toEntity(input)).thenReturn(entity);
        when(accessoryRepository.save(entity)).thenReturn(saved);
        when(accessoryMapper.toDto(saved)).thenReturn(output);

        AccessoryDto result = accessoryService.save(input);

        assertEquals(1L, result.id());
        verify(vehicleRepository).existsById(10L);
        verify(accessoryRepository).save(entity);
    }

    @Test
    void should_throw_when_vehicle_not_found_on_save() {
        AccessoryDto input = new AccessoryDto(null, "GPS", "Systeme navigation", 1500L, "Electronique", 99L);
        when(vehicleRepository.existsById(99L)).thenReturn(false);

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> accessoryService.save(input));

        assertEquals("Vehicle with ID 99 not found", ex.getMessage());
        verify(vehicleRepository).existsById(99L);
        verify(accessoryMapper, never()).toEntity(any());
        verify(accessoryRepository, never()).save(any());
    }

    @Test
    void should_delete_accessory_by_id() {
        accessoryService.deleteById(1L);

        verify(accessoryRepository).deleteById(1L);
    }
}

