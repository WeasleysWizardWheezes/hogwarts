package de.thkoeln.ccq.firemanager.calendar.domain;

import java.util.UUID;

public record CalendarId(UUID value) {
    public CalendarId {
        if (value == null) {
            throw new IllegalArgumentException("Calendar ID cannot be null");
        }
    }
    
    public static CalendarId of(UUID value) {
        return new CalendarId(value);
    }
}