package de.thkoeln.ccq.firemanager.equipment.infrastructure;

import de.thkoeln.ccq.firemanager.equipment.domain.Equipment;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentId;
import org.springframework.stereotype.Component;

@Component
public class EquipmentPersistenceMapper {

    public EquipmentJpaEntity toEntity(Equipment domain) {
        return EquipmentJpaEntity.builder()
            .id(domain.id().value())
            .name(domain.name())
            .serialNumber(domain.serialNumber())
            .type(domain.type())
            .location(domain.location())
            .description(domain.description())
            .createdAt(domain.createdAt())
            .updatedAt(domain.updatedAt())
            .build();
    }

    public Equipment toDomain(EquipmentJpaEntity entity) {
        return new Equipment(
            EquipmentId.from(entity.getId()),
            entity.getName(),
            entity.getSerialNumber(),
            entity.getType(),
            entity.getLocation(),
            entity.getDescription(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}