package com.demo.vehicle.api.event;

import com.demo.vehicle.api.dto.VehicleDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class VehicleCreatedEvent extends ApplicationEvent {
    private final VehicleDto vehicle;
    private final LocalDateTime createdAt;

    public VehicleCreatedEvent(Object source, VehicleDto vehicle) {
        super(source);
        this.vehicle = vehicle;
        this.createdAt = LocalDateTime.now();
    }
}

