package de.thkoeln.ccq.firemanager.equipment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

    @Query("""
            SELECT e FROM Equipment e
            WHERE e.archived = false
            AND (:search IS NULL
                OR LOWER(e.name) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(e.inventoryNumber) LIKE LOWER(CONCAT('%', :search, '%')))
            AND (:categoryId IS NULL OR e.category.id = :categoryId)
            AND (:vehicleId IS NULL OR e.vehicle.id = :vehicleId)
            AND (:status IS NULL OR e.status = :status)
            AND (:dueBefore IS NULL
                OR e.nextInspectionDate <= :dueBefore
                OR e.nextMaintenanceDate <= :dueBefore)
            """)
    Page<Equipment> findAllWithFilters(
            @Param("search") String search,
            @Param("categoryId") UUID categoryId,
            @Param("vehicleId") UUID vehicleId,
            @Param("status") EquipmentStatus status,
            @Param("dueBefore") LocalDate dueBefore,
            Pageable pageable
    );

    boolean existsByInventoryNumberAndArchivedFalse(String inventoryNumber);

    boolean existsByInventoryNumberAndIdNotAndArchivedFalse(String inventoryNumber, UUID id);
}
