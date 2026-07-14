package de.thkoeln.ccq.firemanager.equipment.domain;

import java.time.LocalDate;
import java.util.UUID;

public record Equipment(
    EquipmentId id,
    String name,
    String serialNumber,
    String type,
    String storageLocation,
    String description,
    LocalDate acquisitionDate,
    String manufacturer
) {
    public Equipment {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name must not be null or empty");
        }
        if (serialNumber == null || serialNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Serial number must not be null or empty");
        }
        if (type == null || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Type must not be null or empty");
        }
        if (storageLocation == null || storageLocation.trim().isEmpty()) {
            throw new IllegalArgumentException("Storage location must not be null or empty");
        }
    }

    public static Equipment create(
        String name,
        String serialNumber,
        String type,
        String storageLocation,
        String description,
        LocalDate acquisitionDate,
        String manufacturer
    ) {
        return new Equipment(
            EquipmentId.generate(),
            name,
            serialNumber,
            type,
            storageLocation,
            description,
            acquisitionDate,
            manufacturer
        );
    }
}
