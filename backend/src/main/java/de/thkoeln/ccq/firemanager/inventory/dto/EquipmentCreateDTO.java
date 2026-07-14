package de.thkoeln.ccq.firemanager.inventory.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentCreateDTO {
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "Serial number is required")
    private String serialNumber;
    
    @NotBlank(message = "Type is required")
    private String type;
    
    @NotBlank(message = "Location is required")
    private String location;
}