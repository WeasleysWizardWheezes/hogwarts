package de.thkoeln.ccq.firemanager.vehicle.dto;

import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
import lombok.*;

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