package de.thkoeln.ccq.firemanager.equipment.web;

import lombok.*;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentDto {
    private UUID id;
    private String name;
    private String serialNumber;
}