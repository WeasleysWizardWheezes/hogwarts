package de.thkoeln.ccq.firemanager.vehicle.dto;

import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
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

    private Integer baujahr;

    private String beschreibung;

    private VehicleStatus status;

    private UUID vehicleGroupId;
}