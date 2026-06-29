package de.thkoeln.ccq.firemanager.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDTO {
    private String id;
    private String name;
    private String description;
    private boolean isPublic;
    private String organizationUnitId;
}
