package de.thkoeln.ccq.firemanager.dto;

import java.util.List;

public record AvailableEquipmentResponse(
    List<EquipmentSummary> equipment
) {
}