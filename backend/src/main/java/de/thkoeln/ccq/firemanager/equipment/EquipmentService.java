package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentConflictException;
import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentNotFoundException;
import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategoryService;
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
        if (this.equipmentRepository.existsByInventoryNumberAndArchivedFalse(inventoryNumber)) {
            throw new EquipmentConflictException(
                    "Equipment with inventoryNumber '" + inventoryNumber + "' already exists"
            );
        }

        EquipmentCategory category = this.equipmentCategoryService.getById(categoryId);

        Vehicle vehicle = null;
        if (vehicleId != null) {
            vehicle = this.vehicleService.getById(vehicleId);
        }

        if (status == null) {
            status = EquipmentStatus.VERFUEGBAR;
        }

        Equipment equipment = new Equipment(
                name, inventoryNumber, description, status,
                category, vehicle, nextInspectionDate, nextMaintenanceDate
        );
        Equipment saved = this.equipmentRepository.save(equipment);

        EquipmentHistory history = new EquipmentHistory(saved, null, saved.getStatus());
        this.equipmentHistoryRepository.save(history);

        return saved;
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
        String searchParam = (search != null && !search.isBlank()) ? search : null;
        return this.equipmentRepository.findAllWithFilters(
                searchParam, categoryId, vehicleId, status, dueBefore, pageable
        );
    }

    public Equipment getById(UUID equipmentId) {
        if (equipmentId == null) {
            throw new IllegalArgumentException("equipmentId must not be null");
        }
        return this.equipmentRepository.findById(equipmentId)
                .filter(e -> !e.isArchived())
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

        if (name != null && !name.isBlank()) {
            equipment.setName(name);
        }
        if (inventoryNumber != null && !inventoryNumber.isBlank()) {
            boolean conflict = this.equipmentRepository
                    .existsByInventoryNumberAndIdNotAndArchivedFalse(inventoryNumber, equipmentId);
            if (conflict) {
                throw new EquipmentConflictException(
                        "Equipment with inventoryNumber '" + inventoryNumber + "' already exists"
                );
            }
            equipment.setInventoryNumber(inventoryNumber);
        }
        equipment.setDescription(description);

        if (categoryId != null) {
            EquipmentCategory category = this.equipmentCategoryService.getById(categoryId);
            equipment.setCategory(category);
        }

        if (vehicleIdPresent) {
            if (vehicleId != null) {
                Vehicle vehicle = this.vehicleService.getById(vehicleId);
                equipment.setVehicle(vehicle);
            } else {
                equipment.setVehicle(null);
            }
        }

        if (nextInspectionDatePresent) {
            equipment.setNextInspectionDate(nextInspectionDate);
        }
        if (nextMaintenanceDatePresent) {
            equipment.setNextMaintenanceDate(nextMaintenanceDate);
        }

        if (status != null && !status.equals(equipment.getStatus())) {
            EquipmentStatus previousStatus = equipment.getStatus();
            equipment.setStatus(status);
            Equipment saved = this.equipmentRepository.save(equipment);
            saved.setUpdatedAt(OffsetDateTime.now());
            EquipmentHistory history = new EquipmentHistory(saved, previousStatus, status);
            this.equipmentHistoryRepository.save(history);
            return this.equipmentRepository.save(saved);
        }

        equipment.setUpdatedAt(OffsetDateTime.now());
        return this.equipmentRepository.save(equipment);
    }

    public void deleteById(UUID equipmentId) {
        Equipment equipment = getById(equipmentId);
        EquipmentStatus previousStatus = equipment.getStatus();
        equipment.setArchived(true);
        equipment.setStatus(EquipmentStatus.ARCHIVIERT);
        equipment.setUpdatedAt(OffsetDateTime.now());
        Equipment saved = this.equipmentRepository.save(equipment);

        EquipmentHistory history = new EquipmentHistory(saved, previousStatus, EquipmentStatus.ARCHIVIERT);
        this.equipmentHistoryRepository.save(history);
    }

    public List<EquipmentHistory> getHistory(UUID equipmentId) {
        Equipment equipment = getById(equipmentId);
        return this.equipmentHistoryRepository
                .findByEquipmentIdOrderByChangedAtAsc(equipment.getId());
    }
}
