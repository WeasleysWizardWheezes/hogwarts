package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.dto.CreateVehicleRequest;
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

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(
            @Valid @RequestBody CreateVehicleRequest request
    ) {
        var vehicle = vehicleService.create(
                request.name(),
                request.funkrufname(),
                request.kennzeichen(),
                request.baujahr(),
                request.beschreibung(),
                request.status(),
                request.gruppeId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(vehicle);
    }

    @GetMapping
    public ResponseEntity<List<Vehicle>> getAll(
            @RequestParam(required = false) VehicleStatus status,
            @RequestParam(required = false) UUID gruppeId
    ) {
        if (status != null) {
            var vehicles = vehicleService.getByStatus(status);
            return ResponseEntity.status(HttpStatus.OK).body(vehicles);
        } else if (gruppeId != null) {
            var vehicles = vehicleService.getByGruppeId(gruppeId);
            return ResponseEntity.status(HttpStatus.OK).body(vehicles);
        } else {
            var vehicles = vehicleService.getAll();
            return ResponseEntity.status(HttpStatus.OK).body(vehicles);
        }
    }

    @GetMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> getVehicleById(
            @PathVariable UUID vehicleId
    ) {
        Vehicle vehicle = this.vehicleService.getById(vehicleId);
        return ResponseEntity.ok(vehicle);
    }

    @PatchMapping("/{vehicleId}")
    public ResponseEntity<Vehicle> updateVehicle(
            @PathVariable UUID vehicleId,
            @Valid @RequestBody CreateVehicleRequest request
    ) {
        var vehicle = vehicleService.update(
                vehicleId,
                request.name(),
                request.funkrufname(),
                request.kennzeichen(),
                request.baujahr(),
                request.beschreibung(),
                request.status()
        );
        return ResponseEntity.ok(vehicle);
    }

    @DeleteMapping("/{vehicleId}")
    public ResponseEntity<Void> deleteVehicle(
            @PathVariable UUID vehicleId
    ) {
        this.vehicleService.deleteById(vehicleId);
        return ResponseEntity.noContent().build();
    }
}
