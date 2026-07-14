package com.hogwarts.reminder.dto;

import com.hogwarts.reminder.Reminder;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderDto {
    private UUID id;
    private UUID eventId;
    private Reminder.EventType eventType;
    private String title;
    private String description;
    private LocalDateTime reminderTime;
    private int leadTimeMinutes;
    private Reminder.ReminderChannel channel;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
