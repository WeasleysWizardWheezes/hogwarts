package de.thkoeln.ccq.vehicle.controller;

import de.thkoeln.ccq.vehicle.VehicleGroup;
import de.thkoeln.ccq.vehicle.dto.VehicleGroupRequest;
import de.thkoeln.ccq.vehicle.service.VehicleGroupService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicle-groups")
@RequiredArgsConstructor
public class VehicleGroupController {

    private final VehicleGroupService vehicleGroupService;

    @PostMapping
    public ResponseEntity<VehicleGroup> createVehicleGroup(
            @Valid @RequestBody VehicleGroupRequest request) {
        VehicleGroup vehicleGroup = vehicleGroupService.createVehicleGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleGroup);
    }

    @GetMapping
    public ResponseEntity<List<VehicleGroup>> getAllVehicleGroups() {
        List<VehicleGroup> vehicleGroups = vehicleGroupService.getAllVehicleGroups();
        return ResponseEntity.ok(vehicleGroups);
    }

    @GetMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> getVehicleGroupById(@PathVariable UUID vehicleGroupId) {
        VehicleGroup vehicleGroup = vehicleGroupService.getVehicleGroupById(vehicleGroupId);
        return ResponseEntity.ok(vehicleGroup);
    }

    @PatchMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> updateVehicleGroup(
            @PathVariable UUID vehicleGroupId, @Valid @RequestBody VehicleGroupRequest request) {
        VehicleGroup vehicleGroup = vehicleGroupService.updateVehicleGroup(vehicleGroupId, request);
        return ResponseEntity.ok(vehicleGroup);
    }

    @DeleteMapping("/{vehicleGroupId}")
    public ResponseEntity<Void> deleteVehicleGroup(@PathVariable UUID vehicleGroupId) {
        vehicleGroupService.deleteVehicleGroup(vehicleGroupId);
        return ResponseEntity.noContent().build();
    }
}
