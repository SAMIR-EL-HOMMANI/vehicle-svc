package com.demo.vehicle.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VehicleDto(
        Long id,
        String brand,
        String model,
        Integer year,
        String energy,
        GarageDto garage
) {}
