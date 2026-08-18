package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.generated.api.EquipmentApi;
import de.thkoeln.ccq.firemanager.generated.model.CreateEquipmentRequest;
import de.thkoeln.ccq.firemanager.generated.model.EquipmentHistoryResponse;
import de.thkoeln.ccq.firemanager.generated.model.EquipmentResponse;
import de.thkoeln.ccq.firemanager.generated.model.ListEquipment200Response;
import de.thkoeln.ccq.firemanager.generated.model.PaginationMeta;
import de.thkoeln.ccq.firemanager.generated.model.UpdateEquipmentRequest;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
public class EquipmentController implements EquipmentApi {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @Override
    public ResponseEntity<EquipmentResponse> createEquipment(CreateEquipmentRequest createEquipmentRequest) {
        EquipmentStatus status = null;
        if (createEquipmentRequest.getStatus() != null) {
            status = EquipmentStatus.valueOf(createEquipmentRequest.getStatus().getValue());
        }

        UUID vehicleId = null;
        if (createEquipmentRequest.getVehicleId() != null
                && createEquipmentRequest.getVehicleId().isPresent()) {
            vehicleId = createEquipmentRequest.getVehicleId().get();
        }

        LocalDate nextInspectionDate = null;
        if (createEquipmentRequest.getNextInspectionDate() != null
                && createEquipmentRequest.getNextInspectionDate().isPresent()) {
            nextInspectionDate = createEquipmentRequest.getNextInspectionDate().get();
        }

        LocalDate nextMaintenanceDate = null;
        if (createEquipmentRequest.getNextMaintenanceDate() != null
                && createEquipmentRequest.getNextMaintenanceDate().isPresent()) {
            nextMaintenanceDate = createEquipmentRequest.getNextMaintenanceDate().get();
        }

        Equipment equipment = this.equipmentService.create(
                createEquipmentRequest.getName(),
                createEquipmentRequest.getInventoryNumber(),
                createEquipmentRequest.getDescription(),
                status,
                createEquipmentRequest.getCategoryId(),
                vehicleId,
                nextInspectionDate,
                nextMaintenanceDate
        );
        URI location = URI.create("/api/v1/equipment/" + equipment.getId());
        return ResponseEntity.created(location).body(toResponse(equipment));
    }

    @Override
    public ResponseEntity<Void> deleteEquipment(UUID equipmentId) {
        this.equipmentService.deleteById(equipmentId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<EquipmentResponse> getEquipment(UUID equipmentId) {
        Equipment equipment = this.equipmentService.getById(equipmentId);
        return ResponseEntity.ok(toResponse(equipment));
    }

    @Override
    public ResponseEntity<List<EquipmentHistoryResponse>> getEquipmentHistory(UUID equipmentId) {
        List<EquipmentHistory> history = this.equipmentService.getHistory(equipmentId);
        List<EquipmentHistoryResponse> response = history.stream()
                .map(this::toHistoryResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListEquipment200Response> listEquipment(
            Integer page,
            Integer size,
            String search,
            UUID categoryId,
            UUID vehicleId,
            String status,
            LocalDate dueBefore
    ) {
        EquipmentStatus equipmentStatus = null;
        if (status != null) {
            equipmentStatus = EquipmentStatus.valueOf(status);
        }

        Page<Equipment> equipmentPage = this.equipmentService.getAll(
                page, size, search, categoryId, vehicleId, equipmentStatus, dueBefore
        );

        List<EquipmentResponse> data = equipmentPage.getContent().stream()
                .map(this::toResponse)
                .toList();

        PaginationMeta paginationMeta = new PaginationMeta()
                .page(equipmentPage.getNumber())
                .size(equipmentPage.getSize())
                .totalElements((int) equipmentPage.getTotalElements())
                .totalPages(equipmentPage.getTotalPages());

        ListEquipment200Response response = new ListEquipment200Response()
                .data(data)
                .page(paginationMeta);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<EquipmentResponse> updateEquipment(
            UUID equipmentId,
            UpdateEquipmentRequest updateEquipmentRequest
    ) {
        EquipmentStatus status = null;
        if (updateEquipmentRequest.getStatus() != null) {
            status = EquipmentStatus.valueOf(updateEquipmentRequest.getStatus().getValue());
        }

        JsonNullable<UUID> vehicleIdNullable = updateEquipmentRequest.getVehicleId();
        boolean vehicleIdPresent = vehicleIdNullable != null && vehicleIdNullable.isPresent();
        UUID vehicleId = vehicleIdPresent ? vehicleIdNullable.get() : null;

        JsonNullable<LocalDate> nextInspectionNullable = updateEquipmentRequest.getNextInspectionDate();
        boolean nextInspectionPresent = nextInspectionNullable != null && nextInspectionNullable.isPresent();
        LocalDate nextInspectionDate = nextInspectionPresent ? nextInspectionNullable.get() : null;

        JsonNullable<LocalDate> nextMaintenanceNullable = updateEquipmentRequest.getNextMaintenanceDate();
        boolean nextMaintenancePresent = nextMaintenanceNullable != null && nextMaintenanceNullable.isPresent();
        LocalDate nextMaintenanceDate = nextMaintenancePresent ? nextMaintenanceNullable.get() : null;

        Equipment equipment = this.equipmentService.update(
                equipmentId,
                updateEquipmentRequest.getName(),
                updateEquipmentRequest.getInventoryNumber(),
                updateEquipmentRequest.getDescription(),
                status,
                updateEquipmentRequest.getCategoryId(),
                vehicleId,
                vehicleIdPresent,
                nextInspectionDate,
                nextInspectionPresent,
                nextMaintenanceDate,
                nextMaintenancePresent
        );
        return ResponseEntity.ok(toResponse(equipment));
    }

    private EquipmentResponse toResponse(Equipment equipment) {
        EquipmentResponse response = new EquipmentResponse()
                .id(equipment.getId())
                .name(equipment.getName())
                .inventoryNumber(equipment.getInventoryNumber())
                .description(equipment.getDescription())
                .status(EquipmentResponse.StatusEnum.fromValue(equipment.getStatus().name()))
                .categoryId(equipment.getCategory().getId())
                .categoryName(equipment.getCategory().getName())
                .createdAt(equipment.getCreatedAt())
                .updatedAt(equipment.getUpdatedAt());

        if (equipment.getVehicle() != null) {
            response.vehicleId(equipment.getVehicle().getId());
            response.vehicleName(equipment.getVehicle().getName());
        }
        if (equipment.getNextInspectionDate() != null) {
            response.nextInspectionDate(equipment.getNextInspectionDate());
        }
        if (equipment.getNextMaintenanceDate() != null) {
            response.nextMaintenanceDate(equipment.getNextMaintenanceDate());
        }

        return response;
    }

    private EquipmentHistoryResponse toHistoryResponse(EquipmentHistory history) {
        EquipmentHistoryResponse response = new EquipmentHistoryResponse()
                .id(history.getId())
                .equipmentId(history.getEquipment().getId())
                .changedAt(history.getChangedAt());

        if (history.getPreviousStatus() != null) {
            response.previousStatus(
                    EquipmentHistoryResponse.PreviousStatusEnum.fromValue(
                            history.getPreviousStatus().name()
                    )
            );
        }
        if (history.getNewStatus() != null) {
            response.newStatus(
                    EquipmentHistoryResponse.NewStatusEnum.fromValue(
                            history.getNewStatus().name()
                    )
            );
        }

        return response;
    }
}
