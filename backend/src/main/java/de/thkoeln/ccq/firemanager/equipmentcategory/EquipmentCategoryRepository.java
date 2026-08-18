package de.thkoeln.ccq.firemanager.equipmentcategory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EquipmentCategoryRepository extends JpaRepository<EquipmentCategory, UUID> {

    Page<EquipmentCategory> findByArchivedFalse(Pageable pageable);

    Page<EquipmentCategory> findByNameContainingIgnoreCaseAndArchivedFalse(String name, Pageable pageable);

    boolean existsByNameAndArchivedFalse(String name);

    boolean existsByNameAndIdNotAndArchivedFalse(String name, UUID id);
}
