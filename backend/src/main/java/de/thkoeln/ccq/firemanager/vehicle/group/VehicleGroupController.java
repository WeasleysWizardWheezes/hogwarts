package de.thkoeln.ccq.firemanager.vehicle.group;

import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
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
@RequestMapping("/api/v1/vehicle-groups")
@Tag(name = "Fahrzeuggruppen", description = "API für Fahrzeuggruppen")
@RequiredArgsConstructor
public class VehicleGroupController {

    private final VehicleGroupService vehicleGroupService;

    @PostMapping
    @Operation(summary = "Erstelle eine neue Fahrzeuggruppe")
    public ResponseEntity<VehicleGroup> createVehicleGroup(@Valid @RequestBody VehicleGroupRequest request) {
        VehicleGroup createdGroup = vehicleGroupService.createVehicleGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGroup);
    }

    @GetMapping
    @Operation(summary = "Hole alle Fahrzeuggruppen")
    public ResponseEntity<List<VehicleGroup>> getAllVehicleGroups(@RequestParam(defaultValue = "false") boolean isArchived) {
        List<VehicleGroup> groups = vehicleGroupService.getAllVehicleGroups(isArchived);
        return ResponseEntity.ok(groups);
    }

    @GetMapping("/{vehicleGroupId}")
    @Operation(summary = "Hole eine Fahrzeuggruppe nach ID")
    public ResponseEntity<VehicleGroup> getVehicleGroupById(@PathVariable UUID vehicleGroupId) {
        VehicleGroup group = vehicleGroupService.getVehicleGroupById(vehicleGroupId);
        return ResponseEntity.ok(group);
    }

    @PatchMapping("/{vehicleGroupId}")
    @Operation(summary = "Aktualisiere eine Fahrzeuggruppe")
    public ResponseEntity<VehicleGroup> updateVehicleGroup(
            @PathVariable UUID vehicleGroupId,
            @Valid @RequestBody VehicleGroupUpdateRequest request) {
        VehicleGroup updatedGroup = vehicleGroupService.updateVehicleGroup(vehicleGroupId, request);
        return ResponseEntity.ok(updatedGroup);
    }

    @DeleteMapping("/{vehicleGroupId}")
    @Operation(summary = "Archiviere eine Fahrzeuggruppe")
    public ResponseEntity<Void> archiveVehicleGroup(@PathVariable UUID vehicleGroupId) {
        vehicleGroupService.archiveVehicleGroup(vehicleGroupId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{vehicleGroupId}/vehicles")
    @Operation(summary = "Hole alle Fahrzeuge einer Fahrzeuggruppe")
    public ResponseEntity<List<Vehicle>> getVehiclesByGroupId(@PathVariable UUID vehicleGroupId) {
        List<Vehicle> vehicles = vehicleGroupService.getVehiclesByGroupId(vehicleGroupId);
        return ResponseEntity.ok(vehicles);
    }
}