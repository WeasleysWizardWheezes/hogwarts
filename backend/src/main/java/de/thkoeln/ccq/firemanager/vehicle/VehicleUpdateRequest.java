package de.thkoeln.ccq.firemanager.vehicle;

import jakarta.validation.constraints.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleUpdateRequest {
    
    @NotBlank(message = "Name ist ein Pflichtfeld")
    private String name;
    
    @NotBlank(message = "Funkrufname ist ein Pflichtfeld")
    private String funkrufname;
    
    @NotBlank(message = "Kennzeichen ist ein Pflichtfeld")
    private String kennzeichen;
    
    @Min(value = 1900, message = "Baujahr muss mindestens 1900 sein")
    @Max(value = 2100, message = "Baujahr darf nicht in der Zukunft liegen")
    private Integer baujahr;
    
    private String beschreibung;
    
    @NotNull(message = "Fahrzeuggruppen-ID ist ein Pflichtfeld")
    private UUID vehicleGroupId;
    
    @NotNull(message = "Status ist ein Pflichtfeld")
    private VehicleStatus status;
}