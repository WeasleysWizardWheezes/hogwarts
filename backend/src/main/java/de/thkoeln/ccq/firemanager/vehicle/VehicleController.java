package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.generated.api.VehiclesApi;
import de.thkoeln.ccq.firemanager.generated.model.CreateVehicleRequest;
import de.thkoeln.ccq.firemanager.generated.model.ListVehicles200Response;
import de.thkoeln.ccq.firemanager.generated.model.PaginationMeta;
import de.thkoeln.ccq.firemanager.generated.model.UpdateVehicleRequest;
import de.thkoeln.ccq.firemanager.generated.model.VehicleResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class VehicleController implements VehiclesApi {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Override
    public ResponseEntity<VehicleResponse> createVehicle(CreateVehicleRequest createVehicleRequest) {
        VehicleStatus status = null;
        if (createVehicleRequest.getStatus() != null) {
            status = VehicleStatus.valueOf(createVehicleRequest.getStatus().getValue());
        }

        Vehicle vehicle = vehicleService.create(
                createVehicleRequest.getName(),
                createVehicleRequest.getFunkrufname(),
                createVehicleRequest.getKennzeichen(),
                createVehicleRequest.getBaujahr(),
                createVehicleRequest.getBeschreibung(),
                status,
                createVehicleRequest.getVehicleGroupId()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(vehicle));
    }

    @Override
    public ResponseEntity<Void> deleteVehicle(UUID vehicleId) {
        vehicleService.deleteById(vehicleId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<VehicleResponse> getVehicle(UUID vehicleId) {
        Vehicle vehicle = vehicleService.getById(vehicleId);
        return ResponseEntity.ok(toResponse(vehicle));
    }

    @Override
    public ResponseEntity<ListVehicles200Response> listVehicles(
            Integer page,
            Integer size,
            UUID vehicleGroupId,
            String status
    ) {
        VehicleStatus vehicleStatus = null;
        if (status != null) {
            vehicleStatus = VehicleStatus.valueOf(status);
        }

        Page<Vehicle> vehiclePage = vehicleService.getAll(page, size, vehicleGroupId, vehicleStatus);

        List<VehicleResponse> data = vehiclePage.getContent().stream()
                .map(this::toResponse)
                .toList();

        PaginationMeta paginationMeta = new PaginationMeta()
                .page(vehiclePage.getNumber())
                .size(vehiclePage.getSize())
                .totalElements((int) vehiclePage.getTotalElements())
                .totalPages(vehiclePage.getTotalPages());

        ListVehicles200Response response = new ListVehicles200Response()
                .data(data)
                .page(paginationMeta);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<VehicleResponse> updateVehicle(
            UUID vehicleId,
            UpdateVehicleRequest updateVehicleRequest
    ) {
        VehicleStatus status = null;
        if (updateVehicleRequest.getStatus() != null) {
            status = VehicleStatus.valueOf(updateVehicleRequest.getStatus().getValue());
        }

        Vehicle vehicle = vehicleService.update(
                vehicleId,
                updateVehicleRequest.getName(),
                updateVehicleRequest.getFunkrufname(),
                updateVehicleRequest.getKennzeichen(),
                updateVehicleRequest.getBaujahr(),
                updateVehicleRequest.getBeschreibung(),
                status,
                updateVehicleRequest.getVehicleGroupId()
        );
        return ResponseEntity.ok(toResponse(vehicle));
    }

    private VehicleResponse toResponse(Vehicle vehicle) {
        return new VehicleResponse()
                .id(vehicle.getId())
                .name(vehicle.getName())
                .funkrufname(vehicle.getFunkrufname())
                .kennzeichen(vehicle.getKennzeichen())
                .baujahr(vehicle.getBaujahr())
                .beschreibung(vehicle.getBeschreibung())
                .status(VehicleResponse.StatusEnum.fromValue(vehicle.getStatus().name()))
                .vehicleGroupId(vehicle.getVehicleGroup().getId())
                .vehicleGroupName(vehicle.getVehicleGroup().getName())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt());
    }
}
