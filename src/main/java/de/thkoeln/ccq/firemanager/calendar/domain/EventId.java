package de.thkoeln.ccq.firemanager.calendar.domain;

import java.util.UUID;

public record EventId(UUID value) {
    public EventId {
        if (value == null) {
            throw new IllegalArgumentException("Event ID cannot be null");
        }
    }
    
    public static EventId of(UUID value) {
        return new EventId(value);
    }
}