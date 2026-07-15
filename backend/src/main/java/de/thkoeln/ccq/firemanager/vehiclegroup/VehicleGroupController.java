package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.vehiclegroup.dto.CreateVehicleGroupRequest;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicle-groups")
public class VehicleGroupController {

    private final VehicleGroupService vehicleGroupService;

    public VehicleGroupController(VehicleGroupService vehicleGroupService) {
        this.vehicleGroupService = vehicleGroupService;
    }

    @PostMapping
    public ResponseEntity<VehicleGroup> createVehicleGroup(
            @Valid @RequestBody CreateVehicleGroupRequest request
    ) {
        var vehicleGroup = vehicleGroupService.create(
                request.name(),
                request.beschreibung()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicleGroup);
    }

    @GetMapping
    public ResponseEntity<List<VehicleGroup>> getAll() {
        var vehicleGroups = vehicleGroupService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(vehicleGroups);
    }

    @GetMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> getVehicleGroupById(
            @PathVariable UUID vehicleGroupId
    ) {
        VehicleGroup vehicleGroup = this.vehicleGroupService.getById(vehicleGroupId);
        return ResponseEntity.ok(vehicleGroup);
    }

    @PatchMapping("/{vehicleGroupId}")
    public ResponseEntity<VehicleGroup> updateVehicleGroup(
            @PathVariable UUID vehicleGroupId,
            @Valid @RequestBody CreateVehicleGroupRequest request
    ) {
        var vehicleGroup = vehicleGroupService.update(
                vehicleGroupId,
                request.name(),
                request.beschreibung()
        );
        return ResponseEntity.ok(vehicleGroup);
    }

    @DeleteMapping("/{vehicleGroupId}")
    public ResponseEntity<Void> deleteVehicleGroup(
            @PathVariable UUID vehicleGroupId
    ) {
        this.vehicleGroupService.deleteById(vehicleGroupId);
        return ResponseEntity.noContent().build();
    }
}
