package de.thkoeln.ccq.firemanager.vehicle.group.api;

import de.thkoeln.ccq.firemanager.vehicle.group.domain.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehicle.group.application.VehicleGroupService;
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
public class VehicleGroupController {
    private final VehicleGroupService service;

    public VehicleGroupController(VehicleGroupService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VehicleGroup> create(@RequestBody VehicleGroup group) {
        VehicleGroup created = service.create(group);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<VehicleGroup>> getAll() {
        List<VehicleGroup> groups = service.getAll();
        return new ResponseEntity<>(groups, HttpStatus.OK);
    }

    @GetMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> getById(@PathVariable UUID vehicleGroupId) {
        VehicleGroup group = service.getById(vehicleGroupId);
        return new ResponseEntity<>(group, HttpStatus.OK);
    }

    @PatchMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> update(@PathVariable UUID vehicleGroupId, @RequestBody VehicleGroup group) {
        VehicleGroup updated = service.update(vehicleGroupId, group);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{vehicleGroupId}")
    public ResponseEntity<Void> archive(@PathVariable UUID vehicleGroupId) {
        service.archive(vehicleGroupId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}