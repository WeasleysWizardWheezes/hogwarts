package de.thkoeln.ccq.firemanager.vehicle.api;

import de.thkoeln.ccq.firemanager.vehicle.domain.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.domain.VehicleStatus;
import de.thkoeln.ccq.firemanager.vehicle.application.VehicleService;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleRequest;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleUpdateRequest;
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
@RequestMapping("/api/v1/vehicles")
public class VehicleController {
    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Vehicle> create(@Valid @RequestBody VehicleRequest request) {
        Vehicle created = service.create(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAll(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) UUID vehicleGroupId,
            @RequestParam(required = false) Boolean isArchived) {
        List<Vehicle> vehicles = service.getAll(status, vehicleGroupId, isArchived);
        return new ResponseEntity<>(vehicles, HttpStatus.OK);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> getById(@PathVariable UUID vehicleId) {
        Vehicle vehicle = service.getById(vehicleId);
        return new ResponseEntity<>(vehicle, HttpStatus.OK);
    }

    @PatchMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> update(
            @PathVariable UUID vehicleId,
            @Valid @RequestBody VehicleUpdateRequest request) {
        Vehicle updated = service.update(vehicleId, request);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> archive(@PathVariable UUID vehicleId) {
        service.archive(vehicleId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}