package de.thkoeln.ccq.firemanager.calendar.application;

import java.util.UUID;

public class EventNotFoundException extends RuntimeException {
    private static final String ERROR_CODE = "EVENT_NOT_FOUND";
    
    public EventNotFoundException(UUID eventId) {
        super("Event with ID %s not found".formatted(eventId));
    }
    
    public String getErrorCode() {
        return ERROR_CODE;
    }
}