package com.demo.vehicle.api.service;


import com.demo.vehicle.api.dto.GarageDto;
import com.demo.vehicle.api.entity.Garage;
import com.demo.vehicle.api.entity.GarageOpeningHour;
import com.demo.vehicle.api.exception.ResourceNotFoundException;
import com.demo.vehicle.api.exception.ServiceException;
import com.demo.vehicle.api.mapper.GarageMapper;
import com.demo.vehicle.api.repository.GarageRepository;
import com.demo.vehicle.api.repository.VehicleRepository;
import com.demo.vehicle.api.service.impl.GarageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GarageServiceTest {

    @Mock GarageRepository garageRepository;
    @Mock VehicleRepository vehicleRepository;
    @Mock GarageMapper garageMapper;

    @InjectMocks
    GarageServiceImpl garageService;

    private Garage garage;
    private GarageDto garageDto;

    @BeforeEach
    void setUp() {
        garage = Garage.builder()
                .id(1L)
                .name("DACIA CASA")
                .address("123, Boulevard Ghandi, Casablanca")
                .phone_number("0522020222")
                .email("garage@example.com")
                .garageOpeningHours(buildGarageOpeningHours())
                .build();

        garageDto = GarageDto.builder()
                .id(1L)
                .name("DACIA CASA")
                .address("123, Boulevard Ghandi, Casablanca")
                .phoneNumber("0522020222")
                .email("garage@example.com")
                .build();
    }

    @Test
    void should_return_garage_by_id() {
        when(garageRepository.findById(1L)).thenReturn(Optional.of(garage));

        Garage result = garageService.getByIdOrThrow(1L);

        assertEquals(1L, result.getId());
        verify(garageRepository).findById(1L);
    }

    @Test
    void should_throw_when_garage_not_found() {
        when(garageRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> garageService.getByIdOrThrow(1L));

        assertTrue(ex.getMessage().contains("not found"));
        verify(garageRepository).findById(1L);
    }

    @Test
    void should_return_garage_dto_with_working_hours() {
        when(garageRepository.findById(1L)).thenReturn(Optional.of(garage));
        when(garageMapper.toDto(garage)).thenReturn(garageDto);

        GarageDto result = garageService.findGarageByIdWithWorkingHours(1L);

        assertEquals(garageDto, result);
        verify(garageRepository).findById(1L);
        verify(garageMapper).toDto(garage);
    }

    @Test
    void should_add_garage() {
        when(garageMapper.toEntity(garageDto)).thenReturn(garage);
        when(garageRepository.save(garage)).thenReturn(garage);
        when(garageMapper.toDto(garage)).thenReturn(garageDto);

        GarageDto result = garageService.addGarage(garageDto);

        assertEquals(garageDto, result);
        verify(garageMapper).toEntity(garageDto);
        verify(garageRepository).save(garage);
        verify(garageMapper).toDto(garage);
    }

    @Test
    void should_update_garage_when_it_exists() {
        when(garageRepository.existsById(1L)).thenReturn(true);
        when(garageMapper.toEntity(garageDto)).thenReturn(garage);
        when(garageRepository.save(garage)).thenReturn(garage);
        when(garageMapper.toDto(garage)).thenReturn(garageDto);

        GarageDto result = garageService.updateGarage(garageDto);

        assertEquals(garageDto, result);
        verify(garageRepository).existsById(1L);
        verify(garageRepository).save(garage);
    }

    @Test
    void should_throw_when_updating_unknown_garage() {
        when(garageRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> garageService.updateGarage(garageDto));

        verify(garageRepository).existsById(1L);
        verify(garageRepository, never()).save(any());
    }

    @Test
    void should_delete_by_id() {
        garageService.deleteById(5L);

        verify(garageRepository).deleteById(5L);
    }

    @Test
    void should_throw_when_garage_reaches_capacity() {
        when(vehicleRepository.countByGarageId(9L)).thenReturn(50L);

        ServiceException ex = assertThrows(ServiceException.class, () -> garageService.checkGarageAvailability(9L));

        assertTrue(ex.getMessage().contains("maximum amount of vehicles"));
    }

    @Test
    void should_not_throw_when_garage_under_capacity() {
        when(vehicleRepository.countByGarageId(9L)).thenReturn(49L);

        assertDoesNotThrow(() -> garageService.checkGarageAvailability(9L));
    }

    @Test
    void should_list_garages_with_default_id_sort_when_orders_not_allowed() {
        var page = new PageImpl<>(List.of(garage), PageRequest.of(0, 2), 1);
        when(garageRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(garageMapper.toDto(garage)).thenReturn(garageDto);

        var result = garageService.listGarages(0, 2, List.of(new Sort.Order(Sort.Direction.DESC, "hack")));

        assertEquals(1, result.getTotalElements());
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(garageRepository).findAll(pageableCaptor.capture());
        Sort.Order order = pageableCaptor.getValue().getSort().getOrderFor("id");
        assertNotNull(order);
        assertEquals(Sort.Direction.ASC, order.getDirection());
    }

    @Test
    void should_list_garages_with_allowed_sorts_only() {
        var page = new PageImpl<>(List.of(garage), PageRequest.of(0, 2), 1);
        when(garageRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(garageMapper.toDto(garage)).thenReturn(garageDto);

        garageService.listGarages(0, 2, List.of(
                new Sort.Order(Sort.Direction.DESC, "name"),
                new Sort.Order(Sort.Direction.ASC, "unknown")
        ));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(garageRepository).findAll(pageableCaptor.capture());
        assertNotNull(pageableCaptor.getValue().getSort().getOrderFor("name"));
        assertNull(pageableCaptor.getValue().getSort().getOrderFor("unknown"));

    }

    List<GarageOpeningHour> buildGarageOpeningHours() {
        return List.of(GarageOpeningHour.builder()
                .id(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8, 0, 0))
                .endTime(LocalTime.of(18, 0, 0))
                .build());
    }

}
