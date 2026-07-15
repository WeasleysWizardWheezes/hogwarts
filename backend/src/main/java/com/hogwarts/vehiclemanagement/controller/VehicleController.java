package com.hogwarts.vehiclemanagement.controller;

import com.hogwarts.vehiclemanagement.dto.VehicleRequest;
import com.hogwarts.vehiclemanagement.dto.VehicleResponse;
import com.hogwarts.vehiclemanagement.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@Tag(name = "Fahrzeuge", description = "API für die Verwaltung von Fahrzeugen")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "Erstellt ein neues Fahrzeug")
    public ResponseEntity<VehicleResponse> createVehicle(@Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listet alle Fahrzeuge auf")
    public ResponseEntity<List<VehicleResponse>> getAllVehicles(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long vehicleGroupId,
            @RequestParam(required = false) String status,
            @ParameterObject Pageable pageable) {
        Specification<com.hogwarts.vehiclemanagement.entity.Vehicle> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + name + "%"));
        }

        if (vehicleGroupId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("vehicleGroup").get("id"), vehicleGroupId));
        }

        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }

        List<VehicleResponse> responses = vehicleService.getAllVehicles(spec, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ruft ein bestimmtes Fahrzeug ab")
    public ResponseEntity<VehicleResponse> getVehicleById(@PathVariable Long id) {
        VehicleResponse response = vehicleService.getVehicleById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Aktualisiert ein Fahrzeug")
    public ResponseEntity<VehicleResponse> updateVehicle(
            @PathVariable Long id,
            @Valid @RequestBody VehicleRequest request) {
        VehicleResponse response = vehicleService.updateVehicle(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Löscht ein Fahrzeug (Soft Delete)")
    public ResponseEntity<Void> deleteVehicle(@PathVariable Long id) {
        vehicleService.deleteVehicle(id);
        return ResponseEntity.noContent().build();
    }
}
