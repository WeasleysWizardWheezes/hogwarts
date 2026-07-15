package com.hogwarts.vehiclemanagement.controller;

import com.hogwarts.vehiclemanagement.dto.VehicleGroupRequest;
import com.hogwarts.vehiclemanagement.dto.VehicleGroupResponse;
import com.hogwarts.vehiclemanagement.service.VehicleGroupService;
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
@RequestMapping("/api/vehicle-groups")
@Tag(name = "Fahrzeuggruppen", description = "API für die Verwaltung von Fahrzeuggruppen")
@RequiredArgsConstructor
public class VehicleGroupController {

    private final VehicleGroupService vehicleGroupService;

    @PostMapping
    @Operation(summary = "Erstellt eine neue Fahrzeuggruppe")
    public ResponseEntity<VehicleGroupResponse> createVehicleGroup(@Valid @RequestBody VehicleGroupRequest request) {
        VehicleGroupResponse response = vehicleGroupService.createVehicleGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Listet alle Fahrzeuggruppen auf")
    public ResponseEntity<List<VehicleGroupResponse>> getAllVehicleGroups(
            @RequestParam(required = false) String name,
            @ParameterObject Pageable pageable) {
        Specification<com.hogwarts.vehiclemanagement.entity.VehicleGroup> spec = Specification.where(null);

        if (name != null) {
            spec = spec.and((root, query, cb) -> cb.like(root.get("name"), "%" + name + "%"));
        }

        List<VehicleGroupResponse> responses = vehicleGroupService.getAllVehicleGroups(spec, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Ruft eine bestimmte Fahrzeuggruppe ab")
    public ResponseEntity<VehicleGroupResponse> getVehicleGroupById(@PathVariable Long id) {
        VehicleGroupResponse response = vehicleGroupService.getVehicleGroupById(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Aktualisiert eine Fahrzeuggruppe")
    public ResponseEntity<VehicleGroupResponse> updateVehicleGroup(
            @PathVariable Long id,
            @Valid @RequestBody VehicleGroupRequest request) {
        VehicleGroupResponse response = vehicleGroupService.updateVehicleGroup(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Löscht eine Fahrzeuggruppe (Soft Delete)")
    public ResponseEntity<Void> deleteVehicleGroup(@PathVariable Long id) {
        vehicleGroupService.deleteVehicleGroup(id);
        return ResponseEntity.noContent().build();
    }
}
