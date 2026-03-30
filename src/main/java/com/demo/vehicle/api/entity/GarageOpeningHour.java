package com.demo.vehicle.api.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Entity
@Table(name = "garage_opening_hours")
@Data
@Builder
@NoArgsConstructor
public class GarageOpeningHour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private DayOfWeek dayOfWeek;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "garage_id", nullable = false)
    private Garage garage;

    public GarageOpeningHour(Long id, LocalTime startTime, LocalTime endTime, DayOfWeek dayOfWeek, Garage garage) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("startTime et endTime sont obligatoires");
        }
        if (!startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("startTime doit être strictement avant endTime");
        }
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfWeek = dayOfWeek;
        this.garage = garage;
    }

}
