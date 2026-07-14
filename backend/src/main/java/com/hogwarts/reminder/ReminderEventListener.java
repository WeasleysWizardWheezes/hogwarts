package com.hogwarts.reminder;

import com.hogwarts.calendar.EventCreatedEvent;
import com.hogwarts.operation.OperationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ReminderEventListener {

    private final ReminderService reminderService;

    @EventListener
    public void handleEventCreated(EventCreatedEvent event) {
        // Create a reminder for the new event (default: 30 minutes before)
        Reminder reminder = Reminder.builder()
                .eventId(event.getEventId())
                .eventType(Reminder.EventType.APPOINTMENT)
                .title("Reminder: " + event.getTitle())
                .description(event.getDescription())
                .reminderTime(event.getStartTime().minusMinutes(30))
                .leadTimeMinutes(30)
                .channel(Reminder.ReminderChannel.EMAIL)
                .build();

        reminderService.createReminder(reminder);
    }

    @EventListener
    public void handleOperationCreated(OperationCreatedEvent event) {
        // Create a reminder for the new operation (default: 60 minutes before)
        Reminder reminder = Reminder.builder()
                .eventId(event.getOperationId())
                .eventType(Reminder.EventType.OPERATION)
                .title("Reminder: " + event.getTitle())
                .description(event.getDescription())
                .reminderTime(event.getStartTime().minusMinutes(60))
                .leadTimeMinutes(60)
                .channel(Reminder.ReminderChannel.PUSH)
                .build();

        reminderService.createReminder(reminder);
    }
}
