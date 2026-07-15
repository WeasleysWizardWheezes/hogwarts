package de.thkoeln.ccq.vehicle.dto;

import de.thkoeln.ccq.vehicle.VehicleStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {

    @NotBlank(message = "Name is mandatory")
    private String name;

    private String funkrufname;

    private String kennzeichen;

    private Integer baujahr;

    private String beschreibung;

    @NotNull(message = "Status is mandatory")
    private VehicleStatus status;

    @NotNull(message = "Group ID is mandatory")
    private UUID gruppeId;
}
