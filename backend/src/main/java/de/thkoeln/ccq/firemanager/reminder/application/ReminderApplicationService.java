package de.thkoeln.ccq.firemanager.reminder.application;

import de.thkoeln.ccq.firemanager.reminder.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReminderApplicationService {
    
    private final ReminderRepository reminderRepository;
    
    @Transactional(readOnly = true)
    public List<Reminder> getAllReminders() {
        return reminderRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Reminder> getRemindersByUserId(UUID userId) {
        return reminderRepository.findByUserId(userId);
    }
    
    @Transactional(readOnly = true)
    public List<Reminder> getRemindersByChannel(Channel channel) {
        return reminderRepository.findByChannel(channel);
    }
    
    @Transactional
    public Reminder updateReminder(ReminderId reminderId, int newLeadTimeMinutes, Channel newChannel) {
        return reminderRepository.findById(reminderId)
            .map(reminder -> {
                Reminder updatedReminder = reminder;
                if (newLeadTimeMinutes != reminder.leadTimeMinutes()) {
                    updatedReminder = updatedReminder.withLeadTime(newLeadTimeMinutes);
                }
                if (newChannel != reminder.channel()) {
                    updatedReminder = updatedReminder.withChannel(newChannel);
                }
                return reminderRepository.save(updatedReminder);
            })
            .orElseThrow(() -> new ReminderNotFoundException(reminderId));
    }
}