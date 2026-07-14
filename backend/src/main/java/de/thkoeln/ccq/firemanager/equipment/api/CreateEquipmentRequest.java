package de.thkoeln.ccq.firemanager.equipment.api;

import jakarta.validation.constraints.NotBlank;

public record CreateEquipmentRequest(
    @NotBlank String name,
    @NotBlank String serialNumber,
    @NotBlank String type,
    @NotBlank String storageLocation,
    String description,
    String acquisitionDate,
    String manufacturer
) {
}
