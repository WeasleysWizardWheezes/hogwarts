package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.generated.api.VehicleGroupsApi;
import de.thkoeln.ccq.firemanager.generated.model.CreateVehicleGroupRequest;
import de.thkoeln.ccq.firemanager.generated.model.ListVehicleGroups200Response;
import de.thkoeln.ccq.firemanager.generated.model.UpdateVehicleGroupRequest;
import de.thkoeln.ccq.firemanager.generated.model.VehicleGroupResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
public class VehicleGroupController implements VehicleGroupsApi {

    private final VehicleGroupService vehicleGroupService;

    public VehicleGroupController(VehicleGroupService vehicleGroupService) {
        this.vehicleGroupService = vehicleGroupService;
    }

    @Override
    public ResponseEntity<VehicleGroupResponse> createVehicleGroup(
            @Valid CreateVehicleGroupRequest createVehicleGroupRequest
    ) {
        var vehicleGroup = vehicleGroupService.create(
                createVehicleGroupRequest.getName(),
                createVehicleGroupRequest.getDescription()
        );
        VehicleGroupResponse response = mapToResponse(vehicleGroup);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteVehicleGroup(UUID vehicleGroupId) {
        vehicleGroupService.deleteById(vehicleGroupId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<VehicleGroupResponse> getVehicleGroup(UUID vehicleGroupId) {
        var vehicleGroup = vehicleGroupService.getById(vehicleGroupId);
        VehicleGroupResponse response = mapToResponse(vehicleGroup);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListVehicleGroups200Response> listVehicleGroups(Integer page, Integer size) {
        List<VehicleGroup> vehicleGroups = vehicleGroupService.getAll();
        List<VehicleGroupResponse> responses = vehicleGroups.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        ListVehicleGroups200Response listResponse = new ListVehicleGroups200Response();
        listResponse.setData(responses);
        return ResponseEntity.ok(listResponse);
    }

    @Override
    public ResponseEntity<VehicleGroupResponse> updateVehicleGroup(
            UUID vehicleGroupId,
            @Valid UpdateVehicleGroupRequest updateVehicleGroupRequest
    ) {
        var vehicleGroup = vehicleGroupService.update(
                vehicleGroupId,
                updateVehicleGroupRequest.getName(),
                updateVehicleGroupRequest.getDescription()
        );
        VehicleGroupResponse response = mapToResponse(vehicleGroup);
        return ResponseEntity.ok(response);
    }

    private VehicleGroupResponse mapToResponse(VehicleGroup vehicleGroup) {
        VehicleGroupResponse response = new VehicleGroupResponse();
        response.setId(vehicleGroup.getId());
        response.setName(vehicleGroup.getName());
        response.setDescription(vehicleGroup.getDescription());
        response.setCreatedAt(vehicleGroup.getCreatedAt());
        response.setUpdatedAt(vehicleGroup.getUpdatedAt());
        response.setArchived(vehicleGroup.isArchived());
        return response;
    }
}
