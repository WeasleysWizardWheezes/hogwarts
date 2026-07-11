package de.thkoeln.ccq.firemanager.reminder.api;

import de.thkoeln.ccq.firemanager.reminder.domain.Channel;
import de.thkoeln.ccq.firemanager.reminder.domain.EventType;

import java.time.ZonedDateTime;
import java.util.UUID;

public record ReminderResponse(
    UUID id,
    UUID eventId,
    EventType eventType,
    String title,
    String description,
    ZonedDateTime reminderTime,
    int leadTimeMinutes,
    Channel channel,
    ZonedDateTime createdAt,
    ZonedDateTime updatedAt
) {}