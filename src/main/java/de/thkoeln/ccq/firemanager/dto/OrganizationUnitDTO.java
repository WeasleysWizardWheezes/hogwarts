package de.thkoeln.ccq.firemanager.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrganizationUnitDTO {
    private String id;
    private String name;
    private String description;
}
