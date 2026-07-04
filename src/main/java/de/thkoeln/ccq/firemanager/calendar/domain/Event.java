package de.thkoeln.ccq.firemanager.calendar.domain;

import lombok.Builder;
import lombok.Value;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Value
@Builder
public class Event {
    UUID id;
    String title;
    String description;
    LocalDateTime startTime;
    LocalDateTime endTime;
    UUID calendarId;
    boolean recurring;
    String recurrencePattern;
    List<UUID> attendees;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
}