package com.demo.vehicle.api.mapper;


import com.demo.vehicle.api.dto.GarageDto;
import com.demo.vehicle.api.dto.OpeningTimeDto;
import com.demo.vehicle.api.entity.Garage;
import com.demo.vehicle.api.entity.GarageOpeningHour;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface GarageMapper {

    @Mapping(target = "phone_number", source = "phoneNumber")
    @Mapping(target = "garageOpeningHours", source = "GarageOpeningHours")
    Garage toEntity(GarageDto dto);

    @Mapping(target = "phoneNumber", source = "phone_number")
    @Mapping(target = "GarageOpeningHours", source = "garageOpeningHours")
    GarageDto toDto(Garage entity);

    List<Garage> toEntities(List<GarageDto> dtos);
    List<GarageDto> toDtos(List<Garage> entities);

    default List<GarageOpeningHour> map(Map<DayOfWeek, List<OpeningTimeDto>> horairesOuverture) {
        if (horairesOuverture == null) {
            return null;
        }

        List<GarageOpeningHour> result = new ArrayList<>();
        for (Map.Entry<DayOfWeek, List<OpeningTimeDto>> entry : horairesOuverture.entrySet()) {
            DayOfWeek day = entry.getKey();
            List<OpeningTimeDto> slots = entry.getValue();
            if (day == null || slots == null) {
                continue;
            }
            for (OpeningTimeDto slot : slots) {
                if (slot == null) {
                    continue;
                }
                result.add(GarageOpeningHour.builder()
                        .dayOfWeek(day)
                        .startTime(slot.startTime())
                        .endTime(slot.endTime())
                        .build());
            }
        }
        return result;
    }

    default Map<DayOfWeek, List<OpeningTimeDto>> map(List<GarageOpeningHour> openingHours) {
        if (openingHours == null) {
            return null;
        }

        Map<DayOfWeek, List<OpeningTimeDto>> result = new EnumMap<>(DayOfWeek.class);
        openingHours.stream()
                .filter(h -> h != null && h.getDayOfWeek() != null)
                .sorted(Comparator
                        .comparing(GarageOpeningHour::getDayOfWeek)
                        .thenComparing(GarageOpeningHour::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())))
                .forEach(h -> result
                        .computeIfAbsent(h.getDayOfWeek(), d -> new ArrayList<>())
                        .add(OpeningTimeDto.builder()
                                .startTime(h.getStartTime())
                                .endTime(h.getEndTime())
                                .build()));
        return result;
    }

    @AfterMapping
    default void attachBackRefs(@MappingTarget Garage garage) {
        if (garage.getGarageOpeningHours() == null) return;
        for (GarageOpeningHour h : garage.getGarageOpeningHours()) {
            if (h != null) h.setGarage(garage);
        }
    }


}
