package de.thkoeln.ccq.firemanager.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderDTO {
    private String id;
    private String appointmentId;
    private String userId;
    private String timeBefore;
    private String method;
    private boolean isRecurring;
}
