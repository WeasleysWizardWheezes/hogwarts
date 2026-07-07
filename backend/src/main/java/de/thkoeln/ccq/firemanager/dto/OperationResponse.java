package de.thkoeln.ccq.firemanager.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OperationResponse(
    UUID id,
    String title,
    String operationType,
    LocalDateTime startTime,
    LocalDateTime endTime,
    List<UUID> requiredEquipment,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}