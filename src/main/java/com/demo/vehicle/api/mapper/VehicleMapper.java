package com.demo.vehicle.api.mapper;


import com.demo.vehicle.api.dto.VehicleCreateDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.dto.VehicleUpdateDto;
import com.demo.vehicle.api.entity.Vehicle;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VehicleMapper {

    Vehicle toEntity(VehicleDto dto);
    @Mapping(target = "garage.id", source = "garageId")
    Vehicle createDtoToEntity(VehicleCreateDto dto);
    @Mapping(target = "garage.id", source = "garageId")
    Vehicle updateDtoToEntity(VehicleUpdateDto dto);

    VehicleDto toDto(Vehicle entity);

    List<VehicleDto> toDtos(List<Vehicle> entities);

}
