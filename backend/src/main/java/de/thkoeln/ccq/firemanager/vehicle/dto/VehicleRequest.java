package de.thkoeln.ccq.firemanager.vehicle.dto;

import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {
    @NotBlank(message = "Name ist ein Pflichtfeld")
    private String name;

    @NotBlank(message = "Funkrufname ist ein Pflichtfeld")
    private String funkrufname;

    @NotBlank(message = "Kennzeichen ist ein Pflichtfeld")
    private String kennzeichen;

    private Integer baujahr;
    private String beschreibung;
    private VehicleStatus status;

    @NotNull(message = "VehicleGroup-ID ist ein Pflichtfeld")
    private UUID vehicleGroupId;
}