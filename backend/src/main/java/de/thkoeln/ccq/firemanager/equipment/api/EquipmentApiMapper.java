package de.thkoeln.ccq.firemanager.equipment.api;

import de.thkoeln.ccq.firemanager.equipment.domain.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentApiMapper {
    public EquipmentResponse toResponse(Equipment domain) {
        return new EquipmentResponse(
            domain.id().value().toString(),
            domain.name(),
            domain.serialNumber(),
            domain.type(),
            domain.storageLocation(),
            domain.description(),
            domain.acquisitionDate(),
            domain.manufacturer()
        );
    }
}
