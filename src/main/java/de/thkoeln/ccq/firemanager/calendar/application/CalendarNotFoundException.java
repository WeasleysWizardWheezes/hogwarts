package de.thkoeln.ccq.firemanager.calendar.application;

import java.util.UUID;

public class CalendarNotFoundException extends RuntimeException {
    private static final String ERROR_CODE = "CALENDAR_NOT_FOUND";
    
    public CalendarNotFoundException(UUID calendarId) {
        super("Calendar with ID %s not found".formatted(calendarId));
    }
    
    public String getErrorCode() {
        return ERROR_CODE;
    }
}