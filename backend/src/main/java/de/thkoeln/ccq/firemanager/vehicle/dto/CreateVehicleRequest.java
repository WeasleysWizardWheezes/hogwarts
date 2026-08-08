package de.thkoeln.ccq.firemanager.vehicle.dto;

import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateVehicleRequest(
        @NotBlank(message = "name must not empty")
        String name,

        String radioCallName,

        String licensePlate,

        Integer yearOfConstruction,

        String description,

        @NotNull(message = "status must not null")
        Vehicle.VehicleStatus status,

        @NotNull(message = "vehicleGroupId must not null")
        UUID vehicleGroupId
) {
}