package de.thkoeln.ccq.firemanager.vehiclegroup.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateVehicleGroupRequest(
        @NotBlank(message = "name must not empty")
        String name,

        String description
) {
}