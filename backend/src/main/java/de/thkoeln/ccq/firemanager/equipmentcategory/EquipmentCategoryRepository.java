package de.thkoeln.ccq.firemanager.equipmentcategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, UUID> {

    @Query(value = """
            SELECT * FROM equipment_category ec
            WHERE ec.archived = false
            AND (CAST(:search AS text) IS NULL
                OR LOWER(ec.name) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')))
            """,
            countQuery = """
            SELECT count(*) FROM equipment_category ec
            WHERE ec.archived = false
            AND (CAST(:search AS text) IS NULL
                OR LOWER(ec.name) LIKE LOWER(CONCAT('%', CAST(:search AS text), '%')))
            """,
            nativeQuery = true)
    Page<EquipmentCategory> findAllWithSearch(
            @Param("search") String search,
            Pageable pageable
    );

    boolean existsByNameAndArchivedFalse(String name);

    boolean existsByNameAndIdNotAndArchivedFalse(String name, UUID id);
}
