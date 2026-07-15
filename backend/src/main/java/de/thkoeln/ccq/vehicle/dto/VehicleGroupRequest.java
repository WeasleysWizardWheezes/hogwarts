package de.thkoeln.ccq.vehicle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleGroupRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String beschreibung;
}
