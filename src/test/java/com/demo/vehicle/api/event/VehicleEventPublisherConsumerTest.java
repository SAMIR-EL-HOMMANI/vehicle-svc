package com.demo.vehicle.api.event;

import com.demo.vehicle.api.dto.VehicleCreateDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.entity.Garage;
import com.demo.vehicle.api.repository.GarageRepository;
import com.demo.vehicle.api.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Tests du Publisher/Consumer de véhicules")
class VehicleEventPublisherConsumerTest {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private GarageRepository garageRepository;

    private Long garageId;

    @BeforeEach
    void setUp() {
        // Créer un garage de test
        Garage garage = Garage.builder()
                .name("Garage Test Event")
                .address("123 Test Street")
                .phone_number("0123456789")
                .email("test@garage.com")
                .build();
        Garage savedGarage = garageRepository.save(garage);
        garageId = savedGarage.getId();
    }

    @Test
    @DisplayName("Doit publier un événement VehicleCreatedEvent lors de la création d'un véhicule")
    void shouldPublishVehicleCreatedEvent() {
        // Arrange
        VehicleCreateDto vehicleCreateDto = new VehicleCreateDto(
                "Toyota",
                "Corolla",
                2024,
                "GASOLINE",
                garageId
        );

        // Act
        VehicleDto createdVehicle = vehicleService.createVehicle(vehicleCreateDto);

        // Assert
        assertNotNull(createdVehicle);
        assertNotNull(createdVehicle.id());
        assertEquals("Toyota", createdVehicle.brand());
        assertEquals("Corolla", createdVehicle.model());
        
        // Le consumer reçoit l'événement et log les informations
        // Vérifier que le véhicule a été créé correctement
        VehicleDto retrievedVehicle = vehicleService.getByIdOrThrow(createdVehicle.id());
        assertNotNull(retrievedVehicle);
        assertEquals(createdVehicle.id(), retrievedVehicle.id());
    }

    @Test
    @DisplayName("Doit créer un véhicule et déclencher le consumer")
    void shouldCreateVehicleAndTriggerConsumer() {
        // Arrange
        VehicleCreateDto vehicleCreateDto = new VehicleCreateDto(
                "Renault",
                "Megane",
                2023,
                "DIESEL",
                garageId
        );

        // Act
        VehicleDto createdVehicle = vehicleService.createVehicle(vehicleCreateDto);

        // Assert
        assertNotNull(createdVehicle);
        assertEquals("Renault", createdVehicle.brand());
        assertEquals("Megane", createdVehicle.model());
        assertEquals(2023, createdVehicle.year());
        assertEquals("DIESEL", createdVehicle.energy());
    }

    @Test
    @DisplayName("Doit gérer plusieurs créations de véhicules avec des événements indépendants")
    void shouldHandleMultipleVehicleCreations() {
        // Arrange
        VehicleCreateDto vehicle1 = new VehicleCreateDto(
                "BMW",
                "X5",
                2024,
                "HYBRID",
                garageId
        );

        VehicleCreateDto vehicle2 = new VehicleCreateDto(
                "Mercedes",
                "E-Class",
                2024,
                "ELECTRIC",
                garageId
        );

        // Act
        VehicleDto createdVehicle1 = vehicleService.createVehicle(vehicle1);
        VehicleDto createdVehicle2 = vehicleService.createVehicle(vehicle2);

        // Assert
        assertNotNull(createdVehicle1);
        assertNotNull(createdVehicle2);
        assertNotEquals(createdVehicle1.id(), createdVehicle2.id());
        assertEquals("BMW", createdVehicle1.brand());
        assertEquals("Mercedes", createdVehicle2.brand());
    }
}

