package de.thkoeln.ccq.firemanager.vehicle.group;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

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