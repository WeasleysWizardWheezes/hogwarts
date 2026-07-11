package de.thkoeln.ccq.firemanager.reminder.domain;

import java.time.ZonedDateTime;
import java.util.UUID;

public record Reminder(
    ReminderId id,
    UUID eventId,
    EventType eventType,
    String title,
    String description,
    ZonedDateTime reminderTime,
    int leadTimeMinutes,
    Channel channel,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {
    public Reminder {
        if (leadTimeMinutes < 1) {
            throw new IllegalArgumentException("Lead time must be at least 1 minute");
        }
    }
    
    public Reminder withLeadTime(int newLeadTimeMinutes) {
        return new Reminder(
            id,
            eventId,
            eventType,
            title,
            description,
            reminderTime,
            newLeadTimeMinutes,
            channel,
            createdAt,
            ZonedDateTime.now()
        );
    }
    
    public Reminder withChannel(Channel newChannel) {
        return new Reminder(
            id,
            eventId,
            eventType,
            title,
            description,
            reminderTime,
            leadTimeMinutes,
            newChannel,
            createdAt,
            ZonedDateTime.now()
        );
    }
}