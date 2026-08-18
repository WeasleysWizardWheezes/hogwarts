package de.thkoeln.ccq.firemanager.equipment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

    @Query(value = """
            SELECT e.* FROM equipment e
            WHERE e.archived = false
            AND (CAST(:search AS text) IS NULL
                OR LOWER(e.name) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))
                OR LOWER(e.inventory_number) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')))
            AND (CAST(:categoryId AS uuid) IS NULL OR e.category_id = CAST(:categoryId AS uuid))
            AND (CAST(:vehicleId AS uuid) IS NULL OR e.vehicle_id = CAST(:vehicleId AS uuid))
            AND (CAST(:status AS text) IS NULL OR e.status = CAST(:status AS text))
            AND (CAST(:dueBefore AS date) IS NULL
                OR e.next_inspection_date <= CAST(:dueBefore AS date)
                OR e.next_maintenance_date <= CAST(:dueBefore AS date))
            """,
            countQuery = """
            SELECT count(*) FROM equipment e
            WHERE e.archived = false
            AND (CAST(:search AS text) IS NULL
                OR LOWER(e.name) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%'))
                OR LOWER(e.inventory_number) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')))
            AND (CAST(:categoryId AS uuid) IS NULL OR e.category_id = CAST(:categoryId AS uuid))
            AND (CAST(:vehicleId AS uuid) IS NULL OR e.vehicle_id = CAST(:vehicleId AS uuid))
            AND (CAST(:status AS text) IS NULL OR e.status = CAST(:status AS text))
            AND (CAST(:dueBefore AS date) IS NULL
                OR e.next_inspection_date <= CAST(:dueBefore AS date)
                OR e.next_maintenance_date <= CAST(:dueBefore AS date))
            """,
            nativeQuery = true)
        Page<Equipment> findAllWithFiltersByStatusName(
            @Param("search") String search,
            @Param("categoryId") UUID categoryId,
            @Param("vehicleId") UUID vehicleId,
            @Param("status") String status,
            @Param("dueBefore") LocalDate dueBefore,
            Pageable pageable
    );

    default Page<Equipment> findAllWithFilters(
                    String search,
                    UUID categoryId,
                    UUID vehicleId,
                    EquipmentStatus status,
                    LocalDate dueBefore,
                    Pageable pageable
    ) {
        String statusName = status == null ? null : status.name();
        return this.findAllWithFiltersByStatusName(
            search, categoryId, vehicleId, statusName, dueBefore, pageable);
    }

    boolean existsByInventoryNumber(String inventoryNumber);

    boolean existsByInventoryNumberAndIdNot(String inventoryNumber, UUID id);
}