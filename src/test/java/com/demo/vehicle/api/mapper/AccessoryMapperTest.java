package com.demo.vehicle.api.mapper;

import com.demo.vehicle.api.dto.AccessoryDto;
import com.demo.vehicle.api.entity.Accessory;
import com.demo.vehicle.api.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class AccessoryMapperTest {

    private final AccessoryMapper mapper = Mappers.getMapper(AccessoryMapper.class);

    @Test
    void should_map_entity_to_dto() {
        Vehicle vehicle = new Vehicle();
        vehicle.setId(11L);

        Accessory entity = new Accessory();
        entity.setId(1L);
        entity.setName("GPS");
        entity.setDescription("Navigation");
        entity.setPrice(1000L);
        entity.setType("Electronique");
        entity.setVehicle(vehicle);

        AccessoryDto dto = mapper.toDto(entity);

        assertEquals(1L, dto.id());
        assertEquals("GPS", dto.name());
        assertEquals(11L, dto.vehicleId());
    }

    @Test
    void should_map_dto_to_entity() {
        AccessoryDto dto = new AccessoryDto(2L, "Camera", "Aide au stationnement", 900L, "Securite", 15L);

        Accessory entity = mapper.toEntity(dto);

        assertEquals(2L, entity.getId());
        assertEquals("Camera", entity.getName());
        assertNotNull(entity.getVehicle());
        assertEquals(15L, entity.getVehicle().getId());
    }
}

