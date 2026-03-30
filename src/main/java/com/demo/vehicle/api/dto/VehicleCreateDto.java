package com.demo.vehicle.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record VehicleCreateDto(

        @JsonProperty(required = true)
        @NotBlank(message = "Vehicle brand is mandatory")
        String brand,
        @NotBlank(message = "Vehicle model is mandatory")
        @JsonProperty(required = true)
        String model,
        Integer year,
        String energy,
        @NotNull(message = "Garage id is mandatory")
        @JsonProperty(required = true)
        Long garageId
) {}
