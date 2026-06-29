package de.thkoeln.ccq.firemanager.dto;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlarmDTO {
    private String id;
    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isRecurring;
    private String recurrencePattern;
    private String organizationUnitId;
    private String calendarId;
}
