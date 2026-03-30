package com.demo.vehicle.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GarageSummaryDto(
        Long id,
        String name
) {}

