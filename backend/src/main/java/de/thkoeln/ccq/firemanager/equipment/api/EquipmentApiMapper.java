package de.thkoeln.ccq.firemanager.equipment.api;

import de.thkoeln.ccq.firemanager.equipment.domain.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentApiMapper {

    public EquipmentResponse toResponse(Equipment domain) {
        return new EquipmentResponse(
            domain.id().value(),
            domain.name(),
            domain.serialNumber(),
            domain.type(),
            domain.location(),
            domain.description(),
            domain.createdAt(),
            domain.updatedAt()
        );
    }

    public Equipment toDomain(CreateEquipmentRequest request) {
        return Equipment.create(
            request.name(),
            request.serialNumber(),
            request.type(),
            request.location(),
            request.description()
        );
    }

    public Equipment toDomainForUpdate(Equipment existing, UpdateEquipmentRequest request) {
        return existing.withUpdatedFields(
            request.name(),
            request.serialNumber(),
            request.type(),
            request.location(),
            request.description()
        );
    }
}