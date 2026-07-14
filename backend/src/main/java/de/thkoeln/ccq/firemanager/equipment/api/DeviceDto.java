package de.thkoeln.ccq.firemanager.equipment.api;

import java.util.Objects;
import java.util.UUID;

public record DeviceDto(
        UUID id,
        String name,
        String serialNumber,
        String type,
        String location
) {
    public DeviceDto {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(serialNumber, "serialNumber must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(location, "location must not be null");
    }
}