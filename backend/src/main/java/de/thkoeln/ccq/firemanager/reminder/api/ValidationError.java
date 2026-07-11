package de.thkoeln.ccq.firemanager.reminder.api;

public record ValidationError(
    String field,
    String message
) {}