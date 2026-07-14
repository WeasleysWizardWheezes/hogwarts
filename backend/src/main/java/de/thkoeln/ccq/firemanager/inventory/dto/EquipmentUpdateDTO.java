package de.thkoeln.ccq.firemanager.inventory.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentUpdateDTO {
    private String name;
    private String serialNumber;
    private String type;
    private String location;
}