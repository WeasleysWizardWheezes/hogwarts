package com.hogwarts.vehiclemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleGroupRequest {

    @NotBlank(message = "Name ist erforderlich")
    private String name;

    private String description;
}
