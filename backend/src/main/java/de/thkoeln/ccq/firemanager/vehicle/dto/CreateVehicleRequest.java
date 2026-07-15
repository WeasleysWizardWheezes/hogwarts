package de.thkoeln.ccq.firemanager.vehicle.dto;

import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateVehicleRequest(
        @NotBlank(message = "name must not be empty")
        String name,

        String funkrufname,

        String kennzeichen,

        Integer baujahr,

        String beschreibung,

        @NotNull(message = "status must not be null")
        VehicleStatus status,

        @NotNull(message = "gruppeId must not be null")
        UUID gruppeId
) {
}