package de.thkoeln.ccq.firemanager.equipment.domain;

import java.util.UUID;

public record EquipmentId(UUID value) {
    public static EquipmentId generate() {
        return new EquipmentId(UUID.randomUUID());
    }
    
    public static EquipmentId from(UUID value) {
        return new EquipmentId(value);
    }
}