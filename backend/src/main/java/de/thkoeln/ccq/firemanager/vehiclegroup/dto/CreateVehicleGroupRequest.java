package de.thkoeln.ccq.firemanager.vehiclegroup.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateVehicleGroupRequest(
        @NotBlank(message = "name must not empty")
        String name,

        @NotBlank(message = "beschreibung must not empty")
        String beschreibung
) {
}
