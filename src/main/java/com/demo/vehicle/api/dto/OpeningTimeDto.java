package com.demo.vehicle.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalTime;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OpeningTimeDto(
        LocalTime startTime,
        LocalTime endTime
) {
}

