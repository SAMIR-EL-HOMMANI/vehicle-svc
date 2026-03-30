package com.demo.vehicle.api.controller;

import com.demo.vehicle.api.dto.GarageDto;
import com.demo.vehicle.api.dto.GarageSummaryDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.service.GarageService;
import com.demo.vehicle.api.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/garages")
public class GarageController {
    private final GarageService garageService;
    private final VehicleService vehicleService;

    public GarageController(GarageService garageService, VehicleService vehicleService) {
        this.garageService = garageService;
        this.vehicleService = vehicleService;
    }

    @GetMapping
    public ResponseEntity<Page<GarageDto>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(name = "sort", required = false) List<String> sortParams
    ) {
        List<Sort.Order> orders = parseSort(sortParams);
        Page<GarageDto> result = garageService.listGarages(page, size, orders);
        return ResponseEntity.ok(result);
    }

    private List<Sort.Order> parseSort(List<String> sortParams) {
        if (CollectionUtils.isEmpty(sortParams)) {
            return List.of(new Sort.Order(Sort.Direction.ASC, "name"));
        }
        List<Sort.Order> orders = new ArrayList<>();
        for (String p : sortParams) {
            String[] parts = p.split(",", 2);
            String property = parts[0].trim();
            Sort.Direction direction = (parts.length > 1 ? Sort.Direction.fromOptionalString(parts[1].trim()).orElse(Sort.Direction.ASC) : Sort.Direction.ASC);
            orders.add(new Sort.Order(direction, property));
        }
        return orders;
    }

    @GetMapping("/{id}")
    public ResponseEntity<GarageDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(garageService.findGarageByIdWithWorkingHours(id));
    }

    @GetMapping("/{id}/vehicles")
    public ResponseEntity<List<VehicleDto>> getVehiclesByGarageId(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.findByGarageId(id));
    }

    @GetMapping("/vehicles")
    public ResponseEntity<List<VehicleDto>> getVehiclesByModelAndGarageIds(
            @RequestParam String model,
            @RequestParam List<Long> garageIds
    ) {
        return ResponseEntity.ok(vehicleService.findByModelAndGarageIds(model, garageIds));
    }

    @GetMapping({"/search/by-accessory", "/search/byAccessory"})
    public ResponseEntity<List<GarageSummaryDto>> getGaragesByAccessoryName(
            @RequestParam String accessoryName
    ) {
        List<GarageSummaryDto> garages = garageService.findGaragesByAccessoryName(accessoryName);
        return ResponseEntity.ok(garages);
    }

    @PostMapping
    public ResponseEntity<GarageDto> create(@Valid @RequestBody GarageDto garage) {
        GarageDto createdGarage = garageService.addGarage(garage);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdGarage.id())
                .toUri();
        return ResponseEntity.created(location).body(createdGarage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GarageDto> update(@PathVariable Long id, @Valid @RequestBody GarageDto garageDto) {
        if (garageDto.id() == null || !id.equals(garageDto.id())) {
            throw new com.demo.vehicle.api.exception.PathBodyIdMismatchException("Path id and body id must be the same");
        }

        return ResponseEntity.ok(garageService.updateGarage(garageDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        garageService.getByIdOrThrow(id);
        garageService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
