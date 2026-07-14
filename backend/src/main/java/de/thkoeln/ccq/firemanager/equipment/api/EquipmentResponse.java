package de.thkoeln.ccq.firemanager.equipment.api;

import java.time.LocalDate;

public record EquipmentResponse(
    String id,
    String name,
    String serialNumber,
    String type,
    String storageLocation,
    String description,
    LocalDate acquisitionDate,
    String manufacturer
) {
}
