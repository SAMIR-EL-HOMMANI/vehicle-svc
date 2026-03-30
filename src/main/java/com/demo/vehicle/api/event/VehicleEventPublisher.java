package com.demo.vehicle.api.event;

import com.demo.vehicle.api.dto.VehicleDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VehicleEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public VehicleEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishVehicleCreatedEvent(VehicleDto vehicle) {
        log.info("Publishing VehicleCreatedEvent for vehicle ID: {}", vehicle.id());
        VehicleCreatedEvent event = new VehicleCreatedEvent(this, vehicle);
        eventPublisher.publishEvent(event);
    }
}

