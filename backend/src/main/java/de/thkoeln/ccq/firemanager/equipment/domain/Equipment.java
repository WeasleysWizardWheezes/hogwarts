package de.thkoeln.ccq.firemanager.equipment.domain;

import java.time.Instant;

public record Equipment(
    EquipmentId id,
    String name,
    String serialNumber,
    String type,
    String location,
    String description,
    Instant createdAt,
    Instant updatedAt
) {
    public static Equipment create(
        String name,
        String serialNumber,
        String type,
        String location,
        String description
    ) {
        var now = Instant.now();
        return new Equipment(
            EquipmentId.generate(),
            name,
            serialNumber,
            type,
            location,
            description,
            now,
            now
        );
    }
    
    public Equipment withUpdatedFields(
        String name,
        String serialNumber,
        String type,
        String location,
        String description
    ) {
        return new Equipment(
            this.id,
            name,
            serialNumber,
            type,
            location,
            description,
            this.createdAt,
            Instant.now()
        );
    }
}