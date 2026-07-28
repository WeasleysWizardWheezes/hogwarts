package de.thkoeln.ccq.firemanager.vehicle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleGroupUpdateRequest {

    private String name;

    private String beschreibung;
}