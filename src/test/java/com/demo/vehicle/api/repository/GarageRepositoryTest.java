package com.demo.vehicle.api.repository;

import com.demo.vehicle.api.entity.Garage;
import com.demo.vehicle.api.entity.GarageOpeningHour;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import jakarta.persistence.EntityManagerFactory;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class GarageRepositoryTest {

	@Autowired
	GarageRepository garageRepository;

	@Autowired
	TestEntityManager testEntityManager;

	@Autowired
	EntityManagerFactory entityManagerFactory;

	@Test
	void should_find_garage_by_id_with_opening_hours_loaded() {
		Garage garage = Garage.builder()
				.name("Renault")
				.address("Casablanca")
				.phone_number("0600000000")
				.email("renault@example.com")
				.build();
		testEntityManager.persist(garage);

		GarageOpeningHour openingHour = GarageOpeningHour.builder()
				.dayOfWeek(DayOfWeek.MONDAY)
				.startTime(LocalTime.of(8, 0))
				.endTime(LocalTime.of(18, 0))
				.garage(garage)
				.build();
		testEntityManager.persist(openingHour);
		testEntityManager.flush();
		testEntityManager.clear();

		Optional<Garage> found = garageRepository.findById(garage.getId());

		assertTrue(found.isPresent());
		assertEquals("Renault", found.get().getName());
		assertTrue(entityManagerFactory.getPersistenceUnitUtil().isLoaded(found.get(), "garageOpeningHours"));
		assertEquals(1, found.get().getGarageOpeningHours().size());
	}


}