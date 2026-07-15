package de.thkoeln.ccq.vehicle.controller;

import de.thkoeln.ccq.vehicle.Vehicle;
import de.thkoeln.ccq.vehicle.VehicleStatus;
import de.thkoeln.ccq.vehicle.dto.VehicleRequest;
import de.thkoeln.ccq.vehicle.service.VehicleService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(
            @Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleService.getAllVehicles();
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable UUID vehicleId) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }

    @PatchMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable UUID vehicleId, @Valid @RequestBody VehicleRequest request) {
        Vehicle vehicle = vehicleService.updateVehicle(vehicleId, request);
        return ResponseEntity.ok(vehicle);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(@PathVariable UUID vehicleId) {
        vehicleService.deleteVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(params = "status")
    public ResponseEntity<List<Vehicle>> getVehiclesByStatus(@RequestParam VehicleStatus status) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByStatus(status);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping(params = "gruppeId")
    public ResponseEntity<List<Vehicle>> getVehiclesByGroup(@RequestParam UUID gruppeId) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByGroup(gruppeId);
        return ResponseEntity.ok(vehicles);
    }
}
