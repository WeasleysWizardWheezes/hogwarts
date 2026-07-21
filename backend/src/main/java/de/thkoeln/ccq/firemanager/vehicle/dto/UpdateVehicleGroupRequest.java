package de.thkoeln.ccq.firemanager.vehicle.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateVehicleGroupRequest(
        @NotBlank(message = "name must not be empty")
        String name,

        String description
) {
}