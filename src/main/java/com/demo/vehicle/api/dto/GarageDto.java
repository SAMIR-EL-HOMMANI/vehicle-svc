package com.demo.vehicle.api.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Map;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GarageDto(
        Long id,
        @NotBlank(message = "Garage name must not be blank")
        @Size(min = 2, max = 100, message = "Garage name must be between 2 and 100 characters")
        String name,
        @NotBlank(message = "Address must not be blank")
        @Size(min = 5, max = 255, message = "Address must be between 5 and 255 characters")
        String address,
        @NotBlank(message = "Phone number must not be blank")
        @Pattern(regexp = "^[0-9\\s+\\-()]+$", message = "Phone number is invalid")
        String phoneNumber,
        @NotBlank(message = "Email must not be blank")
        @Email(message = "Email must be valid")
        String email,
        Map<DayOfWeek, List<OpeningTimeDto>> GarageOpeningHours) {}
