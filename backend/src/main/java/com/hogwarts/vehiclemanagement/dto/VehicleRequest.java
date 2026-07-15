package com.hogwarts.vehiclemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {

    @NotBlank(message = "Name ist erforderlich")
    private String name;

    private String radioCallName;

    private String licensePlate;

    private Integer year;

    private String description;

    @NotNull(message = "Fahrzeuggruppen-ID ist erforderlich")
    private Long vehicleGroupId;

    @NotNull(message = "Status ist erforderlich")
    private String status;
}
