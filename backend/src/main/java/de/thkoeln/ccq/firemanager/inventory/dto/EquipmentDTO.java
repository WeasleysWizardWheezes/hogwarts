package de.thkoeln.ccq.firemanager.inventory.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentDTO {
    private Long id;
    private String name;
    private String serialNumber;
    private String type;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}