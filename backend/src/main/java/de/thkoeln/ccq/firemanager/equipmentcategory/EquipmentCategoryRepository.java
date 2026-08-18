package de.thkoeln.ccq.firemanager.equipmentcategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, UUID> {

    @Query("""
            SELECT ec FROM EquipmentCategory ec
            WHERE ec.archived = false
            AND (:search IS NULL OR LOWER(ec.name) LIKE LOWER(CONCAT('%', :search, '%')))
            """)
    Page<EquipmentCategory> findAllWithSearch(
            @Param("search") String search,
            Pageable pageable
    );

    boolean existsByNameAndArchivedFalse(String name);

    boolean existsByNameAndIdNotAndArchivedFalse(String name, UUID id);
}
