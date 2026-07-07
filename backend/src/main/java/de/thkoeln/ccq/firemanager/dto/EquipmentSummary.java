package de.thkoeln.ccq.firemanager.dto;

import java.util.UUID;

public record EquipmentSummary(
    UUID id,
    String name,
    String type,
    String serialNumber
) {
}