package com.demo.vehicle.api.repository;

import com.demo.vehicle.api.dto.GarageSummaryDto;
import com.demo.vehicle.api.entity.Garage;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface GarageRepository extends JpaRepository<Garage, Long> {

    @Query("""
            SELECT DISTINCT new com.demo.vehicle.api.dto.GarageSummaryDto(g.id, g.name)
            FROM Garage g
            JOIN g.vehicles v
            JOIN v.accessories a
            WHERE LOWER(a.name) = LOWER(:accessoryName)
            """)
    List<GarageSummaryDto> findGaragesByAccessoryName(@Param("accessoryName") String accessoryName);

     @Override
     @EntityGraph(attributePaths = {"garageOpeningHours"}, type = EntityGraph.EntityGraphType.FETCH)
     @NonNull Optional<Garage> findById(@NonNull Long id);

     @Lock(LockModeType.PESSIMISTIC_WRITE)
     @Query("select g from Garage g where g.id = :id")
     Optional<Garage> findByIdForUpdate(@Param("id") Long id);


}

