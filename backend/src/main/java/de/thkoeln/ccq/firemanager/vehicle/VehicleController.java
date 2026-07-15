package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.group.VehicleGroup;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles")
@Tag(name = "Fahrzeuge", description = "API für Fahrzeuge")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping
    @Operation(summary = "Erstelle ein neues Fahrzeug")
    public ResponseEntity<Vehicle> createVehicle(@Valid @RequestBody VehicleRequest request) {
        Vehicle createdVehicle = vehicleService.createVehicle(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVehicle);
    }

    @GetMapping
    @Operation(summary = "Hole alle Fahrzeuge")
    public ResponseEntity<List<Vehicle>> getAllVehicles(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) UUID vehicleGroupId,
            @RequestParam(defaultValue = "false") boolean isArchived) {
        List<Vehicle> vehicles = vehicleService.getAllVehicles(status, vehicleGroupId, isArchived);
        return ResponseEntity.ok(vehicles);
    }

    @GetMapping("/{vehicleId}")
    @Operation(summary = "Hole ein Fahrzeug nach ID")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable UUID vehicleId) {
        Vehicle vehicle = vehicleService.getVehicleById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }

    @PatchMapping("/{vehicleId}")
    @Operation(summary = "Aktualisiere ein Fahrzeug")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable UUID vehicleId,
            @Valid @RequestBody VehicleUpdateRequest request) {
        Vehicle updatedVehicle = vehicleService.updateVehicle(vehicleId, request);
        return ResponseEntity.ok(updatedVehicle);
    }

    @DeleteMapping("/{vehicleId}")
    @Operation(summary = "Archiviere ein Fahrzeug")
    public ResponseEntity<Void> archiveVehicle(@PathVariable UUID vehicleId) {
        vehicleService.archiveVehicle(vehicleId);
        return ResponseEntity.noContent().build();
    }
}