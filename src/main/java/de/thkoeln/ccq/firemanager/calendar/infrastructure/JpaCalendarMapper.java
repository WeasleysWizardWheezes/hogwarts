package de.thkoeln.ccq.firemanager.calendar.infrastructure;

import de.thkoeln.ccq.firemanager.calendar.domain.Calendar;
import java.time.LocalDateTime;
import java.util.UUID;

public class JpaCalendarMapper {
    public static Calendar toDomain(JpaCalendarEntity entity) {
        return Calendar.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
    
    public static JpaCalendarEntity toEntity(Calendar calendar) {
        var entity = new JpaCalendarEntity();
        entity.setId(calendar.getId());
        entity.setName(calendar.getName());
        entity.setDescription(calendar.getDescription());
        entity.setCreatedAt(calendar.getCreatedAt());
        entity.setUpdatedAt(calendar.getUpdatedAt());
        return entity;
    }
}