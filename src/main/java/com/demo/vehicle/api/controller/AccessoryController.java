package com.demo.vehicle.api.controller;

import com.demo.vehicle.api.dto.AccessoryDto;
import com.demo.vehicle.api.exception.PathBodyIdMismatchException;
import com.demo.vehicle.api.service.AccessoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accessories")
public class AccessoryController {
    private final AccessoryService accessoryService;

    public AccessoryController(AccessoryService accessoryService) {
        this.accessoryService = accessoryService;
    }

    @GetMapping
    public List<AccessoryDto> getAll() {
        return accessoryService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccessoryDto> getById(@PathVariable Long id) {
        return accessoryService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<AccessoryDto> create(@Valid @RequestBody AccessoryDto accessory) {
        AccessoryDto createdAccessory = accessoryService.save(accessory);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdAccessory.id())
                .toUri();
        return ResponseEntity.created(location).body(createdAccessory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccessoryDto> update(@PathVariable Long id, @Valid @RequestBody AccessoryDto accessory) {
        if (accessory.id() == null || !id.equals(accessory.id())) {
            throw new PathBodyIdMismatchException("Path id and body id must be the same");
        }

        return accessoryService.findById(id)
                .map(existing -> ResponseEntity.ok(accessoryService.save(accessory)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (accessoryService.findById(id).isPresent()) {
            accessoryService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
