package com.demo.vehicle.api.service;

import com.demo.vehicle.api.dto.VehicleCreateDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.dto.VehicleUpdateDto;
import com.demo.vehicle.api.entity.Garage;
import com.demo.vehicle.api.entity.Vehicle;
import com.demo.vehicle.api.event.VehicleEventPublisher;
import com.demo.vehicle.api.exception.ResourceNotFoundException;
import com.demo.vehicle.api.mapper.VehicleMapper;
import com.demo.vehicle.api.repository.VehicleRepository;
import com.demo.vehicle.api.service.impl.VehicleServiceImpl;
import org.junit.jupiter.api.BeforeEach;
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
class VehicleServiceTest {

    @Mock
    VehicleRepository vehicleRepository;
    @Mock
    GarageService garageService;
    @Mock
    VehicleMapper vehicleMapper;
    @Mock
    VehicleEventPublisher eventPublisher;

    @InjectMocks
    VehicleServiceImpl vehicleService;

    private Vehicle vehicle;
    private VehicleDto vehicleDto;

    @BeforeEach
    void setUp() {
        Garage garage = Garage.builder().id(1L).name("Garage A").address("Address").phone_number("000").email("a@a.com").build();

        vehicle = new Vehicle();
        vehicle.setId(10L);
        vehicle.setBrand("Toyota");
        vehicle.setModel("Corolla");
        vehicle.setYear(2020);
        vehicle.setEnergy("Diesel");
        vehicle.setGarage(garage);

        vehicleDto = new VehicleDto(10L, "Toyota", "Corolla", 2020, "Diesel", null);
    }

    @Test
    void should_find_all_vehicles() {
        when(vehicleRepository.findAll()).thenReturn(List.of(vehicle));
        when(vehicleMapper.toDtos(List.of(vehicle))).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.findAll();

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).id());
        verify(vehicleRepository).findAll();
        verify(vehicleMapper).toDtos(List.of(vehicle));
    }

    @Test
    void should_find_vehicles_by_garage_id() {
        when(garageService.getByIdOrThrow(1L)).thenReturn(Garage.builder().id(1L).build());
        when(vehicleRepository.findByGarageId(1L)).thenReturn(List.of(vehicle));
        when(vehicleMapper.toDtos(List.of(vehicle))).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.findByGarageId(1L);

        assertEquals(1, result.size());
        assertEquals(10L, result.get(0).id());
        verify(garageService).getByIdOrThrow(1L);
        verify(vehicleRepository).findByGarageId(1L);
    }

    @Test
    void should_find_vehicles_by_model_and_garage_ids() {
        List<Long> garageIds = List.of(1L, 2L);
        when(vehicleRepository.findByModelIgnoreCaseAndGarageIdIn("Corolla", garageIds)).thenReturn(List.of(vehicle));
        when(vehicleMapper.toDtos(List.of(vehicle))).thenReturn(List.of(vehicleDto));

        List<VehicleDto> result = vehicleService.findByModelAndGarageIds("Corolla", garageIds);

        assertEquals(1, result.size());
        assertEquals("Corolla", result.get(0).model());
        verify(vehicleRepository).findByModelIgnoreCaseAndGarageIdIn("Corolla", garageIds);
        verify(vehicleMapper).toDtos(List.of(vehicle));
    }

    @Test
    void should_throw_when_garage_not_found_for_find_by_garage_id() {
        when(garageService.getByIdOrThrow(404L)).thenThrow(new ResourceNotFoundException("Garage with ID 404 not found"));

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.findByGarageId(404L));
        verify(vehicleRepository, never()).findByGarageId(anyLong());
    }

    @Test
    void should_get_vehicle_by_id() {
        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(vehicle));
        when(vehicleMapper.toDto(vehicle)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.getByIdOrThrow(10L);

        assertEquals(vehicleDto, result);
    }

    @Test
    void should_throw_when_vehicle_not_found() {
        when(vehicleRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.getByIdOrThrow(404L));
    }

    @Test
    void should_create_vehicle() {
        VehicleCreateDto createDto = new VehicleCreateDto("Toyota", "Corolla", 2020, "Diesel", 1L);
        Garage garage = Garage.builder().id(1L).build();

        Vehicle toSave = new Vehicle();
        toSave.setBrand("Toyota");
        toSave.setModel("Corolla");

        Vehicle saved = new Vehicle();
        saved.setId(10L);
        saved.setGarage(garage);

        when(garageService.getByIdOrThrowForUpdate(1L)).thenReturn(garage);
        when(vehicleMapper.createDtoToEntity(createDto)).thenReturn(toSave);
        when(vehicleRepository.save(toSave)).thenReturn(saved);
        when(vehicleMapper.toDto(saved)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.createVehicle(createDto);

        assertEquals(vehicleDto, result);
        assertSame(garage, toSave.getGarage());
        verify(garageService).checkGarageAvailability(1L);
        verify(garageService).getByIdOrThrowForUpdate(1L);
        verify(vehicleRepository).save(toSave);
    }

    @Test
    void should_update_vehicle_without_capacity_check_when_garage_unchanged() {
        VehicleUpdateDto updateDto = new VehicleUpdateDto(10L, "Toyota", "Corolla", 2021, "Hybrid", 1L);

        Vehicle existing = new Vehicle();
        existing.setId(10L);
        existing.setGarage(Garage.builder().id(1L).build());

        Vehicle toSave = new Vehicle();
        toSave.setId(10L);

        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(garageService.getByIdOrThrow(1L)).thenReturn(Garage.builder().id(1L).build());
        when(vehicleMapper.updateDtoToEntity(updateDto)).thenReturn(toSave);
        when(vehicleRepository.save(toSave)).thenReturn(toSave);
        when(vehicleMapper.toDto(toSave)).thenReturn(vehicleDto);

        VehicleDto result = vehicleService.updateVehicle(updateDto);

        assertEquals(vehicleDto, result);
        verify(garageService, never()).checkGarageAvailability(anyLong());
    }

    @Test
    void should_update_vehicle_with_capacity_check_when_garage_changed() {
        VehicleUpdateDto updateDto = new VehicleUpdateDto(10L, "Toyota", "Corolla", 2021, "Hybrid", 2L);

        Vehicle existing = new Vehicle();
        existing.setId(10L);
        existing.setGarage(Garage.builder().id(1L).build());

        Garage newGarage = Garage.builder().id(2L).build();
        Vehicle toSave = new Vehicle();

        when(vehicleRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(garageService.getByIdOrThrowForUpdate(2L)).thenReturn(newGarage);
        when(vehicleMapper.updateDtoToEntity(updateDto)).thenReturn(toSave);
        when(vehicleRepository.save(toSave)).thenReturn(toSave);
        when(vehicleMapper.toDto(toSave)).thenReturn(vehicleDto);

        vehicleService.updateVehicle(updateDto);

        verify(garageService).checkGarageAvailability(2L);
        verify(garageService).getByIdOrThrowForUpdate(2L);
        verify(garageService, never()).getByIdOrThrow(2L);
        assertSame(newGarage, toSave.getGarage());
    }

    @Test
    void should_throw_when_updating_missing_vehicle() {
        VehicleUpdateDto updateDto = new VehicleUpdateDto(10L, "Toyota", "Corolla", 2021, "Hybrid", 2L);
        when(vehicleRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> vehicleService.updateVehicle(updateDto));
        verify(vehicleRepository, never()).save(any());
    }

    @Test
    void should_delete_by_id() {
        vehicleService.deleteById(10L);

        verify(vehicleRepository).deleteById(10L);
    }
}

