package de.thkoeln.ccq.firemanager.reminder.api;

import java.time.ZonedDateTime;
import java.util.List;

public record ErrorResponse(
    String errorCode,
    String message,
    int status,
    ZonedDateTime timestamp,
    List<ValidationError> validationErrors
) {
    public ErrorResponse(String errorCode, String message, int status, ZonedDateTime timestamp) {
        this(errorCode, message, status, timestamp, List.of());
    }
}