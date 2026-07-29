package de.thkoeln.ccq.firemanager.vehicle.group.api;

import de.thkoeln.ccq.firemanager.vehicle.group.domain.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehicle.group.application.VehicleGroupService;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleGroupRequest;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleGroupUpdateRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicle-groups")
public class VehicleGroupController {
    private final VehicleGroupService service;

    public VehicleGroupController(VehicleGroupService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VehicleGroup> create(@Valid @RequestBody VehicleGroupRequest request) {
        VehicleGroup created = service.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VehicleGroup>> getAll(@RequestParam(required = false) Boolean isArchived) {
        List<VehicleGroup> groups = service.getAll(isArchived);
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> getById(@PathVariable UUID vehicleGroupId) {
        VehicleGroup group = service.getById(vehicleGroupId);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PatchMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> update(
            @PathVariable UUID vehicleGroupId,
            @Valid @RequestBody VehicleGroupUpdateRequest request) {
        VehicleGroup updated = service.update(vehicleGroupId, request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{vehicleGroupId}")
    public ResponseEntity<Void> archive(@PathVariable UUID vehicleGroupId) {
        service.archive(vehicleGroupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}