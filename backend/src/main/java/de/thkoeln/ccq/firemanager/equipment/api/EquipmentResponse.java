package de.thkoeln.ccq.firemanager.equipment.api;

import java.time.Instant;
import java.util.UUID;

public record EquipmentResponse(
        UUID id,
        String name,
        String serialNumber,
        String type,
        String location,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}