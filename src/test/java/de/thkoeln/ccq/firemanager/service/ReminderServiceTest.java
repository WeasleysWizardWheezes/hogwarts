package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.ReminderDTO;
import de.thkoeln.ccq.firemanager.model.Reminder;
import de.thkoeln.ccq.firemanager.repository.ReminderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @InjectMocks
    private ReminderService reminderService;

    private ReminderDTO reminderDTO;
    private Reminder reminder;

    @BeforeEach
    void setUp() {
        reminderDTO = ReminderDTO.builder()
                .id("1")
                .appointmentId("app1")
                .userId("user1")
                .timeBefore("PT1H")
                .method("EMAIL")
                .isRecurring(false)
                .build();

        reminder = Reminder.builder()
                .id("1")
                .timeBefore("PT1H")
                .method("EMAIL")
                .isRecurring(false)
                .build();
    }

    @Test
    void createReminder() {
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);

        ReminderDTO result = reminderService.createReminder(reminderDTO);

        assertNotNull(result);
        assertEquals(reminderDTO.getId(), result.getId());
        assertEquals(reminderDTO.getTimeBefore(), result.getTimeBefore());
        assertEquals(reminderDTO.getMethod(), result.getMethod());
    }

    @Test
    void getReminderById() {
        when(reminderRepository.findById("1")).thenReturn(Optional.of(reminder));

        ReminderDTO result = reminderService.getReminderById("1");

        assertNotNull(result);
        assertEquals(reminder.getId(), result.getId());
        assertEquals(reminder.getTimeBefore(), result.getTimeBefore());
    }

    @Test
    void getReminderById_NotFound() {
        when(reminderRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            reminderService.getReminderById("1");
        });
    }

    @Test
    void getAllReminders() {
        List<Reminder> reminders = Arrays.asList(reminder);
        when(reminderRepository.findAll()).thenReturn(reminders);

        List<ReminderDTO> result = reminderService.getAllReminders();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reminder.getId(), result.get(0).getId());
    }

    @Test
    void getRemindersByAppointment() {
        List<Reminder> reminders = Arrays.asList(reminder);
        when(reminderRepository.findByAppointmentId("app1")).thenReturn(reminders);

        List<ReminderDTO> result = reminderService.getRemindersByAppointment("app1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reminder.getId(), result.get(0).getId());
    }

    @Test
    void getRemindersByUser() {
        List<Reminder> reminders = Arrays.asList(reminder);
        when(reminderRepository.findByUserId("user1")).thenReturn(reminders);

        List<ReminderDTO> result = reminderService.getRemindersByUser("user1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reminder.getId(), result.get(0).getId());
    }

    @Test
    void updateReminder() {
        when(reminderRepository.findById("1")).thenReturn(Optional.of(reminder));
        when(reminderRepository.save(any(Reminder.class))).thenReturn(reminder);

        ReminderDTO result = reminderService.updateReminder("1", reminderDTO);

        assertNotNull(result);
        assertEquals(reminderDTO.getId(), result.getId());
        assertEquals(reminderDTO.getTimeBefore(), result.getTimeBefore());
    }

    @Test
    void updateReminder_NotFound() {
        when(reminderRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            reminderService.updateReminder("1", reminderDTO);
        });
    }

    @Test
    void deleteReminder() {
        doNothing().when(reminderRepository).deleteById("1");

        assertDoesNotThrow(() -> {
            reminderService.deleteReminder("1");
        });

        verify(reminderRepository, times(1)).deleteById("1");
    }
}
