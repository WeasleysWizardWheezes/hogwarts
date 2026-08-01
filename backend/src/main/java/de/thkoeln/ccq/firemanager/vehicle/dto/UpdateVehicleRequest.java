package de.thkoeln.ccq.firemanager.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.util.UUID;

public record UpdateVehicleRequest(
        @NotBlank(message = "name must not empty")
        String name,

        @NotBlank(message = "callSign must not empty")
        String callSign,

        @NotBlank(message = "licensePlate must not empty")
        String licensePlate,

        @Positive(message = "yearOfConstruction must be positive")
        int yearOfConstruction,

        String description,

        @NotNull(message = "vehicleGroupId must not be null")
        UUID vehicleGroupId,

        @NotNull(message = "status must not be null")
        VehicleStatus status
) {
    public enum VehicleStatus {
        AVAILABLE,
        IN_USE,
        IN_MAINTENANCE,
        DEFECTIVE
    }
}