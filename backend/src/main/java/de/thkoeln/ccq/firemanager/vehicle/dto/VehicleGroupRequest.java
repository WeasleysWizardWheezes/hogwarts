package de.thkoeln.ccq.firemanager.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
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
public class VehicleGroupRequest {

    @NotBlank(message = "Name ist ein Pflichtfeld")
    private String name;

    private String beschreibung;
}