package com.demo.vehicle.api.mapper;


import com.demo.vehicle.api.dto.GarageDto;
import com.demo.vehicle.api.dto.OpeningTimeDto;
import com.demo.vehicle.api.entity.Garage;
import com.demo.vehicle.api.entity.GarageOpeningHour;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GarageMapperTest {

    private final GarageMapper mapper = Mappers.getMapper(GarageMapper.class);

    @Test
    void should_map_entity_to_dto() {
        var g = Garage.builder()
                .id(1L)
                .name("DACIA CASA")
                .address("123, Boulevard Ghandi, Casablanca")
                .phone_number("0522020222")
                .garageOpeningHours(buildGarageOpeningHours())
                .build();

        GarageDto dto = mapper.toDto(g);

        assertEquals(1L, dto.id());
        assertEquals("DACIA CASA", dto.name());
        assertEquals("123, Boulevard Ghandi, Casablanca", dto.address());
        assertEquals("0522020222", dto.phoneNumber());
        assertNotNull(dto.GarageOpeningHours());
    }

    @Test
    void should_map_dto_to_entity_and_attach_back_refs() {
        GarageDto dto = GarageDto.builder()
                .id(1L)
                .name("DACIA CASA")
                .address("123, Boulevard Ghandi, Casablanca")
                .phoneNumber("0522020222")
                .email("garage@example.com")
                .GarageOpeningHours(Map.of(
                        DayOfWeek.MONDAY,
                        List.of(OpeningTimeDto.builder()
                                .startTime(LocalTime.of(8, 0, 0))
                                .endTime(LocalTime.of(18, 0, 0))
                                .build())
                ))
                .build();

        Garage entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertNotNull(entity.getGarageOpeningHours());
        assertEquals(1, entity.getGarageOpeningHours().size());
        assertSame(entity, entity.getGarageOpeningHours().get(0).getGarage());
    }

    @Test
    void should_map_dto_without_opening_hours() {
        GarageDto dto = GarageDto.builder()
                .id(2L)
                .name("Garage B")
                .address("Rabat")
                .phoneNumber("0600000000")
                .email("garageb@example.com")
                .GarageOpeningHours(null)
                .build();

        Garage entity = mapper.toEntity(dto);

        assertNotNull(entity);
        assertNull(entity.getGarageOpeningHours());
    }

    List<GarageOpeningHour> buildGarageOpeningHours(){
        return List.of(GarageOpeningHour.builder()
                .id(1L)
                .dayOfWeek(DayOfWeek.MONDAY)
                .startTime(LocalTime.of(8,0,0))
                .endTime(LocalTime.of(18,0,0))
                .build());
    }
}
