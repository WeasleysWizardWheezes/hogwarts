package de.thkoeln.ccq.firemanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CreateOperationRequest(
    @NotBlank String title,
    @NotBlank String operationType,
    @NotNull LocalDateTime startTime,
    @NotNull LocalDateTime endTime,
    List<UUID> requiredEquipment,
    String description
) {
}