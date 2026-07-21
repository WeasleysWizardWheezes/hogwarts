package de.thkoeln.ccq.firemanager.vehicle.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateVehicleGroupRequest(
        @NotBlank(message = "name must not be empty")
        String name,

        String description
) {
}