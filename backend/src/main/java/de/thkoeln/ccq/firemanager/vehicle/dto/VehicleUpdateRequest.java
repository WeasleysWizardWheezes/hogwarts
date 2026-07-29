package de.thkoeln.ccq.firemanager.vehicle.dto;

import de.thkoeln.ccq.firemanager.vehicle.domain.VehicleStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleUpdateRequest {

    private String name;

    private String funkrufname;

    private String kennzeichen;

    @Min(value = 1900, message = "Baujahr muss mindestens 1900 sein")
    @Max(value = 2100, message = "Baujahr darf maximal 2100 sein")
    private Integer baujahr;

    private String beschreibung;

    private VehicleStatus status;

    private UUID vehicleGroupId;
}