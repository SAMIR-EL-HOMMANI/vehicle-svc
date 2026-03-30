package com.demo.vehicle.api.repository;

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
class VehicleRepositoryTest {

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    GarageRepository garageRepository;

    @Test
    void should_save_and_find_vehicle_by_id() {
        Garage garage = Garage.builder()
                .name("Garage V")
                .address("Marrakech")
                .phone_number("0622222222")
                .email("garagev@example.com")
                .build();
        Garage savedGarage = garageRepository.save(garage);

        Vehicle vehicle = new Vehicle();
        vehicle.setBrand("Peugeot");
        vehicle.setModel("208");
        vehicle.setYear(2022);
        vehicle.setEnergy("Essence");
        vehicle.setGarage(savedGarage);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        var found = vehicleRepository.findById(savedVehicle.getId());

        assertTrue(found.isPresent());
        assertEquals("Peugeot", found.get().getBrand());
        assertEquals(savedGarage.getId(), found.get().getGarage().getId());
    }

    @Test
    void should_count_vehicles_by_garage_id() {
        Garage garage1 = garageRepository.save(Garage.builder()
                .name("Garage Count 1")
                .address("Casa")
                .phone_number("0644444444")
                .email("count1@example.com")
                .build());

        Garage garage2 = garageRepository.save(Garage.builder()
                .name("Garage Count 2")
                .address("Rabat")
                .phone_number("0655555555")
                .email("count2@example.com")
                .build());

        Vehicle v1 = new Vehicle();
        v1.setBrand("Toyota");
        v1.setModel("Yaris");
        v1.setYear(2020);
        v1.setEnergy("Essence");
        v1.setGarage(garage1);

        Vehicle v2 = new Vehicle();
        v2.setBrand("Renault");
        v2.setModel("Clio");
        v2.setYear(2021);
        v2.setEnergy("Diesel");
        v2.setGarage(garage1);

        Vehicle v3 = new Vehicle();
        v3.setBrand("Dacia");
        v3.setModel("Sandero");
        v3.setYear(2022);
        v3.setEnergy("Essence");
        v3.setGarage(garage2);

        vehicleRepository.save(v1);
        vehicleRepository.save(v2);
        vehicleRepository.save(v3);

        long garage1Count = vehicleRepository.countByGarageId(garage1.getId());
        long garage2Count = vehicleRepository.countByGarageId(garage2.getId());

        assertEquals(2L, garage1Count);
        assertEquals(1L, garage2Count);
    }

    @Test
    void should_find_vehicles_by_model_and_garage_ids() {
        Garage garage1 = garageRepository.save(Garage.builder()
                .name("Garage Filter 1")
                .address("Casa")
                .phone_number("0666666666")
                .email("filter1@example.com")
                .build());

        Garage garage2 = garageRepository.save(Garage.builder()
                .name("Garage Filter 2")
                .address("Rabat")
                .phone_number("0677777777")
                .email("filter2@example.com")
                .build());

        Vehicle v1 = new Vehicle();
        v1.setBrand("Toyota");
        v1.setModel("Corolla");
        v1.setYear(2020);
        v1.setEnergy("Essence");
        v1.setGarage(garage1);

        Vehicle v2 = new Vehicle();
        v2.setBrand("Toyota");
        v2.setModel("COROLLA");
        v2.setYear(2021);
        v2.setEnergy("Hybrid");
        v2.setGarage(garage2);

        Vehicle v3 = new Vehicle();
        v3.setBrand("Toyota");
        v3.setModel("Yaris");
        v3.setYear(2022);
        v3.setEnergy("Essence");
        v3.setGarage(garage2);

        vehicleRepository.save(v1);
        vehicleRepository.save(v2);
        vehicleRepository.save(v3);

        var results = vehicleRepository.findByModelIgnoreCaseAndGarageIdIn("corolla", java.util.List.of(garage1.getId(), garage2.getId()));

        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(v -> "corolla".equalsIgnoreCase(v.getModel())));
    }
}

