package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.generated.api.VehicleGroupsApi;
import de.thkoeln.ccq.firemanager.generated.model.CreateVehicleGroupRequest;
import de.thkoeln.ccq.firemanager.generated.model.ListVehicleGroups200Response;
import de.thkoeln.ccq.firemanager.generated.model.PaginationMeta;
import de.thkoeln.ccq.firemanager.generated.model.UpdateVehicleGroupRequest;
import de.thkoeln.ccq.firemanager.generated.model.VehicleGroupResponse;
import de.thkoeln.ccq.firemanager.vehicle.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class VehicleGroupController implements VehicleGroupsApi {

    private final VehicleGroupService vehicleGroupService;
    private final VehicleService vehicleService;

    public VehicleGroupController(
            VehicleGroupService vehicleGroupService,
            VehicleService vehicleService
    ) {
        this.vehicleGroupService = vehicleGroupService;
        this.vehicleService = vehicleService;
    }

    @Override
    public ResponseEntity<VehicleGroupResponse> createVehicleGroup(
            CreateVehicleGroupRequest createVehicleGroupRequest
    ) {
        VehicleGroup vehicleGroup = vehicleGroupService.create(
                createVehicleGroupRequest.getName(),
                createVehicleGroupRequest.getBeschreibung()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(vehicleGroup));
    }

    @Override
    public ResponseEntity<Void> deleteVehicleGroup(UUID vehicleGroupId) {
        boolean hasNonArchivedVehicles = vehicleService.hasNonArchivedVehiclesInGroup(vehicleGroupId);
        vehicleGroupService.deleteByIdWithConflictCheck(vehicleGroupId, hasNonArchivedVehicles);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<VehicleGroupResponse> getVehicleGroup(UUID vehicleGroupId) {
        VehicleGroup vehicleGroup = vehicleGroupService.getById(vehicleGroupId);
        return ResponseEntity.ok(toResponse(vehicleGroup));
    }

    @Override
    public ResponseEntity<ListVehicleGroups200Response> listVehicleGroups(Integer page, Integer size) {
        Page<VehicleGroup> vehicleGroupPage = vehicleGroupService.getAll(page, size);

        List<VehicleGroupResponse> data = vehicleGroupPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        PaginationMeta paginationMeta = new PaginationMeta()
                .page(vehicleGroupPage.getNumber())
                .size(vehicleGroupPage.getSize())
                .totalElements((int) vehicleGroupPage.getTotalElements())
                .totalPages(vehicleGroupPage.getTotalPages());

        ListVehicleGroups200Response response = new ListVehicleGroups200Response()
                .data(data)
                .page(paginationMeta);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<VehicleGroupResponse> updateVehicleGroup(
            UUID vehicleGroupId,
            UpdateVehicleGroupRequest updateVehicleGroupRequest
    ) {
        VehicleGroup vehicleGroup = vehicleGroupService.update(
                vehicleGroupId,
                updateVehicleGroupRequest.getName(),
                updateVehicleGroupRequest.getBeschreibung()
        );
        return ResponseEntity.ok(toResponse(vehicleGroup));
    }

    private VehicleGroupResponse toResponse(VehicleGroup vehicleGroup) {
        return new VehicleGroupResponse()
                .id(vehicleGroup.getId())
                .name(vehicleGroup.getName())
                .beschreibung(vehicleGroup.getBeschreibung())
                .createdAt(vehicleGroup.getCreatedAt())
                .updatedAt(vehicleGroup.getUpdatedAt());
    }
}
