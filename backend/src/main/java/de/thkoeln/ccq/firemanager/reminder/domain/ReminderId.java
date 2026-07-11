package de.thkoeln.ccq.firemanager.reminder.domain;

import java.util.UUID;

public record ReminderId(UUID value) {
    public static ReminderId generate() {
        return new ReminderId(UUID.randomUUID());
    }
    
    public static ReminderId from(UUID uuid) {
        return new ReminderId(uuid);
    }
}