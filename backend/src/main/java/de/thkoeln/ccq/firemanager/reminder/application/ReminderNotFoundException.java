package de.thkoeln.ccq.firemanager.reminder.application;

import de.thkoeln.ccq.firemanager.reminder.domain.ReminderId;

public class ReminderNotFoundException extends RuntimeException {
    private static final String ERROR_CODE = "REMINDER_NOT_FOUND";
    
    public ReminderNotFoundException(ReminderId reminderId) {
        super("Reminder mit ID %s nicht gefunden".formatted(reminderId.value()));
    }
    
    public String getErrorCode() {
        return ERROR_CODE;
    }
}