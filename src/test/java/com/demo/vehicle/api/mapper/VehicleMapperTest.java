package com.demo.vehicle.api.mapper;

import com.demo.vehicle.api.dto.VehicleCreateDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.dto.VehicleUpdateDto;
import com.demo.vehicle.api.entity.Garage;
import com.demo.vehicle.api.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class VehicleMapperTest {

    private final VehicleMapper mapper = Mappers.getMapper(VehicleMapper.class);

    @Test
    void should_map_create_dto_to_entity() {
        VehicleCreateDto dto = new VehicleCreateDto("Toyota", "Corolla", 2023, "Hybrid", 5L);

        Vehicle entity = mapper.createDtoToEntity(dto);

        assertEquals("Toyota", entity.getBrand());
        assertEquals("Corolla", entity.getModel());
        assertEquals(2023, entity.getYear());
        assertNotNull(entity.getGarage());
        assertEquals(5L, entity.getGarage().getId());
    }

    @Test
    void should_map_update_dto_to_entity() {
        VehicleUpdateDto dto = new VehicleUpdateDto(9L, "Tesla", "Model 3", 2024, "Electric", 3L);

        Vehicle entity = mapper.updateDtoToEntity(dto);

        assertEquals(9L, entity.getId());
        assertEquals("Tesla", entity.getBrand());
        assertNotNull(entity.getGarage());
        assertEquals(3L, entity.getGarage().getId());
    }

    @Test
    void should_map_entity_to_dto() {
        Garage garage = Garage.builder().id(7L).name("G7").address("Casa").phone_number("00").email("g7@example.com").build();
        Vehicle entity = new Vehicle();
        entity.setId(2L);
        entity.setBrand("Renault");
        entity.setModel("Clio");
        entity.setYear(2022);
        entity.setEnergy("Essence");
        entity.setGarage(garage);

        VehicleDto dto = mapper.toDto(entity);

        assertEquals(2L, dto.id());
        assertEquals("Renault", dto.brand());
        assertNotNull(dto.garage());
        assertEquals(7L, dto.garage().id());
    }
}

