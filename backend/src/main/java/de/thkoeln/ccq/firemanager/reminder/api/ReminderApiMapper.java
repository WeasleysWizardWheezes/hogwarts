package de.thkoeln.ccq.firemanager.reminder.api;

import de.thkoeln.ccq.firemanager.reminder.domain.Reminder;
import org.springframework.stereotype.Component;

@Component
public class ReminderApiMapper {
    
    public ReminderResponse toResponse(Reminder domain) {
        return new ReminderResponse(
            domain.id().value(),
            domain.eventId(),
            domain.eventType(),
            domain.title(),
            domain.description(),
            domain.reminderTime(),
            domain.leadTimeMinutes(),
            domain.channel(),
            domain.createdAt(),
            domain.updatedAt()
        );
    }
}