package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.ReminderDTO;
import de.thkoeln.ccq.firemanager.model.Reminder;
import de.thkoeln.ccq.firemanager.repository.ReminderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReminderService {

    private final ReminderRepository reminderRepository;

    public ReminderDTO createReminder(ReminderDTO reminderDTO) {
        Reminder reminder = mapToEntity(reminderDTO);
        Reminder savedReminder = reminderRepository.save(reminder);
        return mapToDTO(savedReminder);
    }

    public ReminderDTO getReminderById(String id) {
        Reminder reminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
        return mapToDTO(reminder);
    }

    public List<ReminderDTO> getAllReminders() {
        return reminderRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReminderDTO> getRemindersByAppointment(String appointmentId) {
        return reminderRepository.findByAppointmentId(appointmentId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ReminderDTO> getRemindersByUser(String userId) {
        return reminderRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ReminderDTO updateReminder(String id, ReminderDTO reminderDTO) {
        Reminder existingReminder = reminderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reminder not found"));
        
        existingReminder.setTimeBefore(reminderDTO.getTimeBefore());
        existingReminder.setMethod(reminderDTO.getMethod());
        existingReminder.setRecurring(reminderDTO.isRecurring());
        
        Reminder updatedReminder = reminderRepository.save(existingReminder);
        return mapToDTO(updatedReminder);
    }

    public void deleteReminder(String id) {
        reminderRepository.deleteById(id);
    }

    private Reminder mapToEntity(ReminderDTO reminderDTO) {
        return Reminder.builder()
                .id(reminderDTO.getId())
                .timeBefore(reminderDTO.getTimeBefore())
                .method(reminderDTO.getMethod())
                .isRecurring(reminderDTO.isRecurring())
                .build();
    }

    private ReminderDTO mapToDTO(Reminder reminder) {
        return ReminderDTO.builder()
                .id(reminder.getId())
                .appointmentId(reminder.getAppointment() != null ? reminder.getAppointment().getId() : null)
                .userId(reminder.getUser() != null ? reminder.getUser().getId() : null)
                .timeBefore(reminder.getTimeBefore())
                .method(reminder.getMethod())
                .isRecurring(reminder.isRecurring())
                .build();
    }
}
