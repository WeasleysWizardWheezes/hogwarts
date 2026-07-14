package com.hogwarts.reminder;

import com.hogwarts.reminder.dto.ReminderDto;
import com.hogwarts.reminder.dto.ReminderUpdateDto;
import com.hogwarts.reminder.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public List<ReminderDto> getAllReminders(UUID userId, Reminder.ReminderChannel channel) {
        List<Reminder> reminders;
        
        if (userId != null && channel != null) {
            // TODO: Implement user-specific filtering when user integration is available
            reminders = reminderRepository.findByChannel(channel);
        } else if (userId != null) {
            // TODO: Implement user-specific filtering when user integration is available
            reminders = reminderRepository.findAll();
        } else if (channel != null) {
            reminders = reminderRepository.findByChannel(channel);
        } else {
            reminders = reminderRepository.findAll();
        }
        
        return reminders.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public ReminderDto updateReminder(UUID reminderId, ReminderUpdateDto updateDto) {
        Reminder reminder = reminderRepository.findById(reminderId)
                .orElseThrow(() -> new EntityNotFoundException("Reminder not found with id: " + reminderId));

        reminder.setLeadTimeMinutes(updateDto.getLeadTimeMinutes());
        reminder.setChannel(updateDto.getChannel());
        
        // Update reminder time based on new lead time
        // TODO: This should be calculated based on the actual event time
        reminder.setReminderTime(LocalDateTime.now().plusMinutes(updateDto.getLeadTimeMinutes()));

        Reminder updatedReminder = reminderRepository.save(reminder);
        return mapToDto(updatedReminder);
    }

    private ReminderDto mapToDto(Reminder reminder) {
        return ReminderDto.builder()
                .id(reminder.getId())
                .eventId(reminder.getEventId())
                .eventType(reminder.getEventType())
                .title(reminder.getTitle())
                .description(reminder.getDescription())
                .reminderTime(reminder.getReminderTime())
                .leadTimeMinutes(reminder.getLeadTimeMinutes())
                .channel(reminder.getChannel())
                .createdAt(reminder.getCreatedAt())
                .updatedAt(reminder.getUpdatedAt())
                .build();
    }

    public ReminderDto createReminder(Reminder reminder) {
        Reminder savedReminder = reminderRepository.save(reminder);
        return mapToDto(savedReminder);
    }
}
