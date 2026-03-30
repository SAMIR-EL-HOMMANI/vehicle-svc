package com.demo.vehicle.api.mapper;

import com.demo.vehicle.api.dto.AccessoryDto;
import com.demo.vehicle.api.entity.Accessory;
import com.demo.vehicle.api.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AccessoryMapper {

    @Mapping(target = "vehicleId", source = "vehicle.id")
    AccessoryDto toDto(Accessory entity);

    List<AccessoryDto> toDtos(List<Accessory> entities);

    @Mapping(target = "vehicle", source = "vehicleId", qualifiedByName = "vehicleFromId")
    Accessory toEntity(AccessoryDto dto);

    @Named("vehicleFromId")
    default Vehicle vehicleFromId(Long vehicleId) {
        if (vehicleId == null) {
            return null;
        }
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleId);
        return vehicle;
    }
}

