package de.thkoeln.ccq.firemanager.equipment.api;

import jakarta.validation.constraints.NotBlank;

public record CreateEquipmentRequest(
        @NotBlank(message = "Name is required")
        String name,
        
        @NotBlank(message = "Serial number is required")
        String serialNumber,
        
        @NotBlank(message = "Type is required")
        String type,
        
        @NotBlank(message = "Location is required")
        String location,
        
        String description
) {}