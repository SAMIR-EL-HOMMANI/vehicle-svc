package com.demo.vehicle.api.controller;

import com.demo.vehicle.api.dto.AccessoryDto;
import com.demo.vehicle.api.dto.VehicleCreateDto;
import com.demo.vehicle.api.dto.VehicleDto;
import com.demo.vehicle.api.dto.VehicleUpdateDto;
import com.demo.vehicle.api.exception.PathBodyIdMismatchException;
import com.demo.vehicle.api.service.AccessoryService;
import com.demo.vehicle.api.service.VehicleService;
import com.demo.vehicle.api.utils.Constants;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
    private final VehicleService vehicleService;
    private final AccessoryService accessoryService;

    public VehicleController(VehicleService vehicleService, AccessoryService accessoryService) {
        this.vehicleService = vehicleService;
        this.accessoryService = accessoryService;
    }

    @GetMapping
    public List<VehicleDto> getAll() {
        return vehicleService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehicleDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(vehicleService.getByIdOrThrow(id));
    }

    @GetMapping("/{id}/accessories")
    public ResponseEntity<List<AccessoryDto>> getAccessoriesByVehicleId(@PathVariable Long id) {
        vehicleService.getByIdOrThrow(id);
        return ResponseEntity.ok(accessoryService.findByVehicleId(id));
    }

    @PostMapping
    public ResponseEntity<VehicleDto> create(@Valid @RequestBody VehicleCreateDto vehicle) {
        VehicleDto createdVehicle = vehicleService.createVehicle(vehicle);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdVehicle.id())
                .toUri();
        return ResponseEntity.created(location).body(createdVehicle);
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehicleDto> update(@PathVariable Long id,@Valid @RequestBody VehicleUpdateDto vehicle) {
        if (!id.equals(vehicle.id())) {
            throw new PathBodyIdMismatchException(Constants.ID_CHECK_MESSAGE);
        }
        vehicleService.getByIdOrThrow(id);
        return ResponseEntity.ok(vehicleService.updateVehicle(vehicle));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        vehicleService.getByIdOrThrow(id);
        vehicleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
