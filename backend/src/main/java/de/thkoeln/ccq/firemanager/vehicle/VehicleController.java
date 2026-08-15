package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.generated.api.VehiclesApi;
import de.thkoeln.ccq.firemanager.generated.model.CreateVehicleRequest;
import de.thkoeln.ccq.firemanager.generated.model.ListVehicles200Response;
import de.thkoeln.ccq.firemanager.generated.model.UpdateVehicleRequest;
import de.thkoeln.ccq.firemanager.generated.model.VehicleResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class VehicleController implements VehiclesApi {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @Override
    public ResponseEntity<VehicleResponse> createVehicle(
            @Valid CreateVehicleRequest createVehicleRequest
    ) {
        var vehicle = vehicleService.create(
                createVehicleRequest.getName(),
                createVehicleRequest.getRadioCallName(),
                createVehicleRequest.getLicensePlate(),
                createVehicleRequest.getYearOfConstruction(),
                createVehicleRequest.getDescription(),
                Vehicle.VehicleStatus.valueOf(
                        createVehicleRequest.getStatus().getValue()
                ),
                createVehicleRequest.getVehicleGroupId()
        );
        VehicleResponse response = mapToResponse(vehicle);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    @Override
    public ResponseEntity<Void> deleteVehicle(UUID vehicleId) {
        vehicleService.deleteById(vehicleId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<VehicleResponse> getVehicle(UUID vehicleId) {
        var vehicle = vehicleService.getById(vehicleId);
        VehicleResponse response = mapToResponse(vehicle);
        return ResponseEntity.ok(response);
    }

    @Override
public ResponseEntity<ListVehicles200Response> listVehicles(
            Integer page, Integer size, UUID vehicleGroupId, String status) {
        List<Vehicle> vehicles;
        if (vehicleGroupId != null && status != null) {
            vehicles = vehicleService.getByVehicleGroupIdAndStatus(
                    vehicleGroupId,
                    Vehicle.VehicleStatus.valueOf(status)
            );
        } else if (vehicleGroupId != null) {
            vehicles = vehicleService.getByVehicleGroupId(vehicleGroupId);
        } else if (status != null) {
            vehicles = vehicleService.getByStatus(Vehicle.VehicleStatus.valueOf(status));
        } else {
            vehicles = vehicleService.getAll();
        }
        List<VehicleResponse> responses = vehicles.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        ListVehicles200Response listResponse = new ListVehicles200Response();
        listResponse.setData(responses);
        return ResponseEntity.ok(listResponse);
    }

    @Override
    public ResponseEntity<VehicleResponse> updateVehicle(
            UUID vehicleId,
            @Valid UpdateVehicleRequest updateVehicleRequest
    ) {
        var status = Vehicle.VehicleStatus.valueOf(
                updateVehicleRequest.getStatus().getValue()
        );
        var vehicle = vehicleService.update(
                vehicleId,
                updateVehicleRequest.getName(),
                updateVehicleRequest.getRadioCallName(),
                updateVehicleRequest.getLicensePlate(),
                updateVehicleRequest.getYearOfConstruction(),
                updateVehicleRequest.getDescription(),
                status,
                updateVehicleRequest.getVehicleGroupId()
        );
        VehicleResponse response = mapToResponse(vehicle);
        return ResponseEntity.ok(response);
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setName(vehicle.getName());
        response.setRadioCallName(vehicle.getRadioCallName());
        response.setLicensePlate(vehicle.getLicensePlate());
        response.setYearOfConstruction(vehicle.getYearOfConstruction());
        response.setDescription(vehicle.getDescription());
        response.setStatus(VehicleResponse.StatusEnum.valueOf(vehicle.getStatus().name()));
        response.setVehicleGroupId(vehicle.getVehicleGroup().getId());
        response.setCreatedAt(vehicle.getCreatedAt());
        response.setUpdatedAt(vehicle.getUpdatedAt());
        response.setArchived(vehicle.isArchived());
        return response;
    }
}
