package de.thkoeln.ccq.firemanager.calendar.infrastructure;

import de.thkoeln.ccq.firemanager.calendar.domain.Event;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class JpaEventMapper {
    public static Event toDomain(JpaEventEntity entity) {
        return Event.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .calendarId(entity.getCalendarId())
                .recurring(entity.getRecurring() != null ? entity.getRecurring() : false)
                .recurrencePattern(entity.getRecurrencePattern())
                .attendees(List.of()) // Simplified - in reality would map from DB
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public static JpaEventEntity toEntity(Event event) {
        var entity = new JpaEventEntity();
        entity.setId(event.getId());
        entity.setTitle(event.getTitle());
        entity.setDescription(event.getDescription());
        entity.setStartTime(event.getStartTime());
        entity.setEndTime(event.getEndTime());
        entity.setCalendarId(event.getCalendarId());
        entity.setRecurring(event.isRecurring());
        entity.setRecurrencePattern(event.getRecurrencePattern());
        entity.setCreatedAt(event.getCreatedAt());
        entity.setUpdatedAt(event.getUpdatedAt());
        return entity;
    }
}