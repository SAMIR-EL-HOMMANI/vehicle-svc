package com.demo.vehicle.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccessoryDto(
        Long id,
        @NotBlank(message = "Accessory name must not be blank")
        @Size(min = 2, max = 100, message = "Accessory name must be between 2 and 100 characters")
        String name,
        String description,
        @NotNull(message = "Price must not be null")
        @Positive(message = "Price must be positive")
        Long price,
        @NotBlank(message = "Type must not be blank")
        String type,
        @NotNull(message = "Vehicle ID must not be null")
        Long vehicleId
) {}

