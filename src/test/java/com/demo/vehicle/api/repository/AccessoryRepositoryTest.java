package com.demo.vehicle.api.repository;

import com.demo.vehicle.api.entity.Accessory;
import com.demo.vehicle.api.entity.Garage;
import com.demo.vehicle.api.entity.Vehicle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class AccessoryRepositoryTest {

    @Autowired
    AccessoryRepository accessoryRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    GarageRepository garageRepository;

    @Test
    void should_save_and_find_accessory_by_id() {
        Garage garage = Garage.builder()
                .name("Garage A")
                .address("Fes")
                .phone_number("0633333333")
                .email("garagea@example.com")
                .build();
        Garage savedGarage = garageRepository.save(garage);

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand("Dacia");
        vehicle.setModel("Sandero");
        vehicle.setYear(2023);
        vehicle.setEnergy("Essence");
        vehicle.setGarage(savedGarage);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        Accessory accessory = new Accessory();
        accessory.setName("GPS");
        accessory.setDescription("Navigation");
        accessory.setPrice(1200L);
        accessory.setType("Electronique");
        accessory.setVehicle(savedVehicle);

        Accessory savedAccessory = accessoryRepository.save(accessory);

        var found = accessoryRepository.findById(savedAccessory.getId());

        assertTrue(found.isPresent());
        assertEquals("GPS", found.get().getName());
        assertEquals(savedVehicle.getId(), found.get().getVehicle().getId());
    }

    @Test
    void should_find_accessories_by_vehicle_id() {
        Garage garage = Garage.builder()
                .name("Garage B")
                .address("Rabat")
                .phone_number("0644444444")
                .email("garageb@example.com")
                .build();
        Garage savedGarage = garageRepository.save(garage);

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand("Renault");
        vehicle.setModel("Clio");
        vehicle.setYear(2022);
        vehicle.setEnergy("Essence");
        vehicle.setGarage(savedGarage);
        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        Accessory accessory = new Accessory();
        accessory.setName("Camera");
        accessory.setDescription("Camera de recul");
        accessory.setPrice(900L);
        accessory.setType("Securite");
        accessory.setVehicle(savedVehicle);
        accessoryRepository.save(accessory);

        var accessories = accessoryRepository.findByVehicleId(savedVehicle.getId());

        assertEquals(1, accessories.size());
        assertEquals("Camera", accessories.get(0).getName());
        assertEquals(savedVehicle.getId(), accessories.get(0).getVehicle().getId());
    }
}

