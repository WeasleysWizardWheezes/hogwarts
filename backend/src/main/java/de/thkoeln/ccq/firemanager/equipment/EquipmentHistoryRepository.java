package de.thkoeln.ccq.firemanager.equipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EquipmentHistoryRepository extends JpaRepository<EquipmentHistory, UUID> {

    List<EquipmentHistory> findByEquipmentIdOrderByChangedAtAsc(UUID equipmentId);
}