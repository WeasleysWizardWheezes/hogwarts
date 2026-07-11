package de.thkoeln.ccq.firemanager.reminder.infrastructure;

import de.thkoeln.ccq.firemanager.reminder.domain.*;
import org.springframework.stereotype.Component;

@Component
public class ReminderPersistenceMapper {
    
    public ReminderJpaEntity toEntity(Reminder domain) {
        return ReminderJpaEntity.builder()
            .id(domain.id().value())
            .eventId(domain.eventId())
            .eventType(domain.eventType())
            .title(domain.title())
            .description(domain.description())
            .reminderTime(domain.reminderTime())
            .leadTimeMinutes(domain.leadTimeMinutes())
            .channel(domain.channel())
            .createdAt(domain.createdAt())
            .updatedAt(domain.updatedAt())
            .build();
    }
    
    public Reminder toDomain(ReminderJpaEntity entity) {
        return new Reminder(
            ReminderId.from(entity.getId()),
            entity.getEventId(),
            entity.getEventType(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getReminderTime(),
            entity.getLeadTimeMinutes(),
            entity.getChannel(),
            entity.getCreatedAt(),
            entity.getUpdatedAt()
        );
    }
}