package de.thkoeln.ccq.firemanager.vehicle.dto;

import de.thkoeln.ccq.firemanager.vehicle.domain.VehicleStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Year;
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

    @Min(value = 1900, message = "Baujahr muss mindestens 1900 sein")
    @Max(value = 2100, message = "Baujahr darf maximal 2100 sein")
    private Integer baujahr;

    private String beschreibung;

    private VehicleStatus status;

    @NotNull(message = "VehicleGroup-ID ist ein Pflichtfeld")
    private UUID vehicleGroupId;
}