package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategoryService;
import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentConflictException;
import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentHistoryRepository equipmentHistoryRepository;
    private final EquipmentCategoryService equipmentCategoryService;
    private final VehicleService vehicleService;

    public EquipmentService(
            EquipmentRepository equipmentRepository,
            EquipmentHistoryRepository equipmentHistoryRepository,
            EquipmentCategoryService equipmentCategoryService,
            VehicleService vehicleService
    ) {
        this.equipmentRepository = equipmentRepository;
        this.equipmentHistoryRepository = equipmentHistoryRepository;
        this.equipmentCategoryService = equipmentCategoryService;
        this.vehicleService = vehicleService;
    }

    public Equipment create(
            String name,
            String inventoryNumber,
            String description,
            EquipmentStatus status,
            UUID categoryId,
            UUID vehicleId,
            LocalDate nextInspectionDate,
            LocalDate nextMaintenanceDate
    ) {
        if (this.equipmentRepository.existsByInventoryNumber(inventoryNumber)) {
            throw new EquipmentConflictException(
                    "Equipment with inventory number '" + inventoryNumber + "' already exists"
            );
        }
        EquipmentCategory category = this.equipmentCategoryService.getById(categoryId);
        Vehicle vehicle = vehicleId == null ? null : this.vehicleService.getById(vehicleId);
        Equipment equipment = new Equipment(
                name,
                inventoryNumber,
                description,
                status == null ? EquipmentStatus.VERFUEGBAR : status,
                category,
                vehicle,
                nextInspectionDate,
                nextMaintenanceDate
        );
        return this.equipmentRepository.save(equipment);
    }

    public Page<Equipment> getAll(
            int page,
            int size,
            String search,
            UUID categoryId,
            UUID vehicleId,
            EquipmentStatus status,
            LocalDate dueBefore
    ) {
        Pageable pageable = PageRequest.of(page, size);
        String searchParam = search != null && !search.isBlank() ? search : null;
        return this.equipmentRepository.findAllWithFilters(
                searchParam, categoryId, vehicleId, status, dueBefore, pageable);
    }

    public Equipment getById(UUID equipmentId) {
        if (equipmentId == null) {
            throw new IllegalArgumentException("equipmentId must not be null");
        }
        return this.equipmentRepository.findById(equipmentId)
                .filter(equipment -> !equipment.isArchived())
                .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
    }

    public Equipment update(
            UUID equipmentId,
            String name,
            String inventoryNumber,
            String description,
            EquipmentStatus status,
            UUID categoryId,
            UUID vehicleId,
            boolean vehicleIdPresent,
            LocalDate nextInspectionDate,
            boolean nextInspectionDatePresent,
            LocalDate nextMaintenanceDate,
            boolean nextMaintenanceDatePresent
    ) {
        Equipment equipment = getById(equipmentId);
        if (inventoryNumber != null && !inventoryNumber.equals(equipment.getInventoryNumber())
                && this.equipmentRepository.existsByInventoryNumberAndIdNot(inventoryNumber, equipmentId)) {
            throw new EquipmentConflictException(
                    "Equipment with inventory number '" + inventoryNumber + "' already exists"
            );
        }
        if (name != null && !name.isBlank()) {
            equipment.setName(name);
        }
        if (inventoryNumber != null && !inventoryNumber.isBlank()) {
            equipment.setInventoryNumber(inventoryNumber);
        }
        equipment.setDescription(description);
        if (status != null && status != equipment.getStatus()) {
            EquipmentHistory history = new EquipmentHistory(equipment, equipment.getStatus(), status);
            equipment.setStatus(status);
            this.equipmentHistoryRepository.save(history);
        }
        if (categoryId != null) {
            equipment.setCategory(this.equipmentCategoryService.getById(categoryId));
        }
        if (vehicleIdPresent) {
            equipment.setVehicle(vehicleId == null ? null : this.vehicleService.getById(vehicleId));
        }
        if (nextInspectionDatePresent) {
            equipment.setNextInspectionDate(nextInspectionDate);
        }
        if (nextMaintenanceDatePresent) {
            equipment.setNextMaintenanceDate(nextMaintenanceDate);
        }
        equipment.setUpdatedAt(OffsetDateTime.now());
        return this.equipmentRepository.save(equipment);
    }

    public void deleteById(UUID equipmentId) {
        Equipment equipment = getById(equipmentId);
        EquipmentHistory history = new EquipmentHistory(
                equipment, equipment.getStatus(), EquipmentStatus.ARCHIVIERT);
        equipment.setStatus(EquipmentStatus.ARCHIVIERT);
        equipment.setArchived(true);
        equipment.setUpdatedAt(OffsetDateTime.now());
        this.equipmentHistoryRepository.save(history);
        this.equipmentRepository.save(equipment);
    }

    public List<EquipmentHistory> getHistory(UUID equipmentId) {
        getById(equipmentId);
        return this.equipmentHistoryRepository.findByEquipmentIdOrderByChangedAtAsc(equipmentId);
    }
}