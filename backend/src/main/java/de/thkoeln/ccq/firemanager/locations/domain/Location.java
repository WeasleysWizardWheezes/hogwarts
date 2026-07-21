package de.thkoeln.ccq.firemanager.locations.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public record Location(
        UUID id,
        String name,
        LocationType type,
        UUID parentLocationId,
        String parentLocationName,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
    public Location {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("Type must not be null");
        }
    }
}
