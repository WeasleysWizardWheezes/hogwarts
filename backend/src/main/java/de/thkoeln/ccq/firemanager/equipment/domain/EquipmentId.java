package de.thkoeln.ccq.firemanager.equipment.domain;

import java.util.UUID;

public record EquipmentId(UUID value) {
    public static EquipmentId generate() {
        return new EquipmentId(UUID.randomUUID());
    }

    public static EquipmentId from(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("EquipmentId value must not be null");
        }
        return new EquipmentId(value);
    }
}
