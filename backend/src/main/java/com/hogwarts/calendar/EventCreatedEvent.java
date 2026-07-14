package com.hogwarts.calendar;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class EventCreatedEvent extends ApplicationEvent {
    private final UUID eventId;
    private final String title;
    private final String description;
    private final LocalDateTime startTime;

    public EventCreatedEvent(Object source, UUID eventId, String title, String description, LocalDateTime startTime) {
        super(source);
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
    }
}
