package de.thkoeln.ccq.firemanager.vehicle.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleGroupUpdateRequest {
    private String name;
    private String beschreibung;
}