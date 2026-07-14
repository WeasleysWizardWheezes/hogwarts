package com.hogwarts.reminder;

import com.hogwarts.reminder.dto.ReminderDto;
import com.hogwarts.reminder.dto.ReminderUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReminderServiceTest {

    @Mock
    private ReminderRepository reminderRepository;

    @InjectMocks
    private ReminderService reminderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllReminders() {
        // Given
        Reminder reminder1 = Reminder.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .eventType(Reminder.EventType.APPOINTMENT)
                .title("Test Reminder 1")
                .description("Description 1")
                .reminderTime(LocalDateTime.now().plusHours(1))
                .leadTimeMinutes(30)
                .channel(Reminder.ReminderChannel.EMAIL)
                .build();

        Reminder reminder2 = Reminder.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .eventType(Reminder.EventType.OPERATION)
                .title("Test Reminder 2")
                .description("Description 2")
                .reminderTime(LocalDateTime.now().plusHours(2))
                .leadTimeMinutes(60)
                .channel(Reminder.ReminderChannel.PUSH)
                .build();

        when(reminderRepository.findAll()).thenReturn(Arrays.asList(reminder1, reminder2));

        // When
        List<ReminderDto> result = reminderService.getAllReminders(null, null);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test Reminder 1", result.get(0).getTitle());
        assertEquals("Test Reminder 2", result.get(1).getTitle());
    }

    @Test
    void testUpdateReminder() {
        // Given
        UUID reminderId = UUID.randomUUID();
        Reminder existingReminder = Reminder.builder()
                .id(reminderId)
                .eventId(UUID.randomUUID())
                .eventType(Reminder.EventType.APPOINTMENT)
                .title("Original Reminder")
                .description("Original Description")
                .reminderTime(LocalDateTime.now().plusHours(1))
                .leadTimeMinutes(30)
                .channel(Reminder.ReminderChannel.EMAIL)
                .build();

        ReminderUpdateDto updateDto = new ReminderUpdateDto();
        updateDto.setLeadTimeMinutes(45);
        updateDto.setChannel(Reminder.ReminderChannel.PUSH);

        when(reminderRepository.findById(reminderId)).thenReturn(java.util.Optional.of(existingReminder));
        when(reminderRepository.save(any(Reminder.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        ReminderDto result = reminderService.updateReminder(reminderId, updateDto);

        // Then
        assertNotNull(result);
        assertEquals(45, result.getLeadTimeMinutes());
        assertEquals(Reminder.ReminderChannel.PUSH, result.getChannel());
    }

    @Test
    void testUpdateReminderNotFound() {
        // Given
        UUID reminderId = UUID.randomUUID();
        ReminderUpdateDto updateDto = new ReminderUpdateDto();
        updateDto.setLeadTimeMinutes(45);
        updateDto.setChannel(Reminder.ReminderChannel.PUSH);

        when(reminderRepository.findById(reminderId)).thenReturn(java.util.Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> {
            reminderService.updateReminder(reminderId, updateDto);
        });
    }
}
