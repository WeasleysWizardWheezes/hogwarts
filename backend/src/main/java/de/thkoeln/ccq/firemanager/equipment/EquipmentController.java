package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.generated.api.EquipmentApi;
import de.thkoeln.ccq.firemanager.generated.model.CreateEquipmentRequest;
import de.thkoeln.ccq.firemanager.generated.model.EquipmentHistoryResponse;
import de.thkoeln.ccq.firemanager.generated.model.EquipmentResponse;
import de.thkoeln.ccq.firemanager.generated.model.ListEquipment200Response;
import de.thkoeln.ccq.firemanager.generated.model.PaginationMeta;
import de.thkoeln.ccq.firemanager.generated.model.UpdateEquipmentRequest;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
public class EquipmentController implements EquipmentApi {

    private final EquipmentService equipmentService;

    public EquipmentController(EquipmentService equipmentService) {
        this.equipmentService = equipmentService;
    }

    @Override
    public ResponseEntity<EquipmentResponse> createEquipment(
            CreateEquipmentRequest request
    ) {
        Equipment equipment = this.equipmentService.create(
                request.getName(),
                request.getInventoryNumber(),
                request.getDescription(),
                request.getStatus() == null
                        ? null : EquipmentStatus.valueOf(request.getStatus().getValue()),
                request.getCategoryId(),
                request.getVehicleId().isPresent() ? request.getVehicleId().get() : null,
                request.getNextInspectionDate().isPresent()
                        ? request.getNextInspectionDate().get() : null,
                request.getNextMaintenanceDate().isPresent()
                        ? request.getNextMaintenanceDate().get() : null
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
        return ResponseEntity.ok(toResponse(this.equipmentService.getById(equipmentId)));
    }

    @Override
    public ResponseEntity<List<EquipmentHistoryResponse>> getEquipmentHistory(UUID equipmentId) {
        List<EquipmentHistoryResponse> response = this.equipmentService.getHistory(equipmentId)
                .stream()
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
            java.time.LocalDate dueBefore
    ) {
        EquipmentStatus equipmentStatus = status == null ? null : EquipmentStatus.valueOf(status);
        Page<Equipment> equipmentPage = this.equipmentService.getAll(
                page, size, search, categoryId, vehicleId, equipmentStatus, dueBefore);
        List<EquipmentResponse> data = equipmentPage.getContent().stream()
                .map(this::toResponse)
                .toList();
        PaginationMeta paginationMeta = new PaginationMeta()
                .page(equipmentPage.getNumber())
                .size(equipmentPage.getSize())
                .totalElements((int) equipmentPage.getTotalElements())
                .totalPages(equipmentPage.getTotalPages());
        return ResponseEntity.ok(new ListEquipment200Response().data(data).page(paginationMeta));
    }

    @Override
    public ResponseEntity<EquipmentResponse> updateEquipment(
            UUID equipmentId,
            UpdateEquipmentRequest request
    ) {
        Equipment equipment = this.equipmentService.update(
                equipmentId,
                request.getName(),
                request.getInventoryNumber(),
                request.getDescription(),
                request.getStatus() == null
                        ? null : EquipmentStatus.valueOf(request.getStatus().getValue()),
                request.getCategoryId(),
                request.getVehicleId().isPresent() ? request.getVehicleId().get() : null,
                request.getVehicleId().isPresent(),
                request.getNextInspectionDate().isPresent()
                        ? request.getNextInspectionDate().get() : null,
                request.getNextInspectionDate().isPresent(),
                request.getNextMaintenanceDate().isPresent()
                        ? request.getNextMaintenanceDate().get() : null,
                request.getNextMaintenanceDate().isPresent()
        );
        return ResponseEntity.status(HttpStatus.OK).body(toResponse(equipment));
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
            response.vehicleId(equipment.getVehicle().getId())
                    .vehicleName(equipment.getVehicle().getName());
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
                .newStatus(EquipmentHistoryResponse.NewStatusEnum.fromValue(
                        history.getNewStatus().name()))
                .changedAt(history.getChangedAt());
        if (history.getPreviousStatus() != null) {
            response.previousStatus(EquipmentHistoryResponse.PreviousStatusEnum.fromValue(
                    history.getPreviousStatus().name()));
        }
        return response;
    }
}