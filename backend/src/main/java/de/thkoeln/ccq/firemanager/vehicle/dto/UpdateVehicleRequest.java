package de.thkoeln.ccq.firemanager.vehicle.dto;

import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateVehicleRequest(
        @NotBlank(message = "name must not be empty")
        String name,

        @NotBlank(message = "radioCallName must not be empty")
        String radioCallName,

        String licensePlate,

        Integer year,

        String description,

        @NotNull
        Vehicle.VehicleStatus status
) {
}