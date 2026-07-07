package de.thkoeln.ccq.firemanager.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record AssignEquipmentRequest(
    @NotEmpty List<UUID> equipmentIds
) {
}