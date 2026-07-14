package de.thkoeln.ccq.firemanager.equipment.infrastructure;

import de.thkoeln.ccq.firemanager.equipment.domain.Equipment;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentId;
import org.springframework.stereotype.Component;

@Component
public class EquipmentPersistenceMapper {
    public EquipmentJpaEntity toEntity(Equipment domain) {
        return new EquipmentJpaEntity(
            domain.id().value(),
            domain.name(),
            domain.serialNumber(),
            domain.type(),
            domain.storageLocation(),
            domain.description(),
            domain.acquisitionDate(),
            domain.manufacturer()
        );
    }

    public Equipment toDomain(EquipmentJpaEntity entity) {
        return new Equipment(
            EquipmentId.from(entity.getId()),
            entity.getName(),
            entity.getSerialNumber(),
            entity.getType(),
            entity.getStorageLocation(),
            entity.getDescription(),
            entity.getAcquisitionDate(),
            entity.getManufacturer()
        );
    }
}
