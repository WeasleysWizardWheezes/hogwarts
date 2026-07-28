package de.thkoeln.ccq.firemanager.vehicle.group;

import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleGroupRequest;
import de.thkoeln.ccq.firemanager.vehicle.dto.VehicleGroupUpdateRequest;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupArchivedException;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleGroupNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicle-groups")
@RequiredArgsConstructor
public class VehicleGroupController {

    private final VehicleGroupService vehicleGroupService;

    @PostMapping
    public ResponseEntity<VehicleGroup> createVehicleGroup(@Valid @RequestBody VehicleGroupRequest request) {
        VehicleGroup vehicleGroup = vehicleGroupService.createVehicleGroup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleGroup);
    }

    @GetMapping
    public ResponseEntity<List<VehicleGroup>> getAllVehicleGroups(@RequestParam(required = false, defaultValue = "false") boolean isArchived) {
        List<VehicleGroup> vehicleGroups = vehicleGroupService.getAllVehicleGroups(isArchived);
        return ResponseEntity.ok(vehicleGroups);
    }

    @GetMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> getVehicleGroupById(@PathVariable UUID vehicleGroupId) {
        VehicleGroup vehicleGroup = vehicleGroupService.getVehicleGroupById(vehicleGroupId);
        return ResponseEntity.ok(vehicleGroup);
    }

    @PatchMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> updateVehicleGroup(
            @PathVariable UUID vehicleGroupId,
            @Valid @RequestBody VehicleGroupUpdateRequest request) {
        VehicleGroup vehicleGroup = vehicleGroupService.updateVehicleGroup(vehicleGroupId, request);
        return ResponseEntity.ok(vehicleGroup);
    }

    @DeleteMapping("/{vehicleGroupId}")
    public ResponseEntity<Void> archiveVehicleGroup(@PathVariable UUID vehicleGroupId) {
        vehicleGroupService.archiveVehicleGroup(vehicleGroupId);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(VehicleGroupNotFoundException.class)
    public ResponseEntity<String> handleVehicleGroupNotFoundException(VehicleGroupNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(VehicleGroupArchivedException.class)
    public ResponseEntity<String> handleVehicleGroupArchivedException(VehicleGroupArchivedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }
}