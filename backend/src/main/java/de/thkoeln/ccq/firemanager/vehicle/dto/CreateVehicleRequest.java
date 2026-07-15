package de.thkoeln.ccq.firemanager.vehicle.dto;

import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateVehicleRequest(
        @NotBlank(message = "name must not empty")
        String name,

        @NotBlank(message = "funkrufname must not empty")
        String funkrufname,

        @NotBlank(message = "kennzeichen must not empty")
        String kennzeichen,

        @NotNull(message = "baujahr must not null")
        Integer baujahr,

        @NotBlank(message = "beschreibung must not empty")
        String beschreibung,

        @NotNull(message = "status must not null")
        VehicleStatus status,

        @NotNull(message = "gruppeId must not null")
        UUID gruppeId
) {
}
