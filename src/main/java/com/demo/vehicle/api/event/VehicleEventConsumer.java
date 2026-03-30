package com.demo.vehicle.api.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class VehicleEventConsumer {

    @EventListener
    public void onVehicleCreated(VehicleCreatedEvent event) {
        log.info("===============================================");
        log.info(" EVENT: Véhicule créé avec succès!");
        log.info("===============================================");
        log.info(" Détails du véhicule:");
        log.info("   - ID: {}", event.getVehicle().id());
        log.info("   - Marque: {}", event.getVehicle().brand());
        log.info("   - Modèle: {}", event.getVehicle().model());
        log.info("   - Année: {}", event.getVehicle().year());
        log.info("   - Énergie: {}", event.getVehicle().energy());
        log.info("   - Garage: {}", event.getVehicle().garage().name());
        log.info("   - Date de création: {}", event.getCreatedAt());
        log.info("===============================================");
    }
}

