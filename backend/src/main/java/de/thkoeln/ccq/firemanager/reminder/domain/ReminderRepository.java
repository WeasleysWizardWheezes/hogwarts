package de.thkoeln.ccq.firemanager.reminder.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReminderRepository {
    Reminder save(Reminder reminder);
    
    Optional<Reminder> findById(ReminderId id);
    
    List<Reminder> findByUserId(UUID userId);
    
    List<Reminder> findByChannel(Channel channel);
    
    List<Reminder> findAll();
}