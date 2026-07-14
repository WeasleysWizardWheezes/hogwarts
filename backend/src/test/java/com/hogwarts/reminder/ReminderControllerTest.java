package com.hogwarts.reminder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hogwarts.reminder.dto.ReminderDto;
import com.hogwarts.reminder.dto.ReminderUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReminderControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReminderService reminderService;

    @InjectMocks
    private ReminderController reminderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(reminderController).build();
    }

    @Test
    void testGetAllReminders() throws Exception {
        // Given
        ReminderDto reminder1 = ReminderDto.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .eventType(Reminder.EventType.APPOINTMENT)
                .title("Test Reminder 1")
                .description("Description 1")
                .reminderTime(LocalDateTime.now().plusHours(1))
                .leadTimeMinutes(30)
                .channel(Reminder.ReminderChannel.EMAIL)
                .build();

        ReminderDto reminder2 = ReminderDto.builder()
                .id(UUID.randomUUID())
                .eventId(UUID.randomUUID())
                .eventType(Reminder.EventType.OPERATION)
                .title("Test Reminder 2")
                .description("Description 2")
                .reminderTime(LocalDateTime.now().plusHours(2))
                .leadTimeMinutes(60)
                .channel(Reminder.ReminderChannel.PUSH)
                .build();

        List<ReminderDto> reminders = Arrays.asList(reminder1, reminder2);
        when(reminderService.getAllReminders(null, null)).thenReturn(reminders);

        // When & Then
        mockMvc.perform(get("/api/v1/reminders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").value("Test Reminder 1"))
                .andExpect(jsonPath("$[1].title").value("Test Reminder 2"));
    }

    @Test
    void testUpdateReminder() throws Exception {
        // Given
        UUID reminderId = UUID.randomUUID();
        ReminderUpdateDto updateDto = new ReminderUpdateDto();
        updateDto.setLeadTimeMinutes(45);
        updateDto.setChannel(Reminder.ReminderChannel.PUSH);

        ReminderDto updatedReminder = ReminderDto.builder()
                .id(reminderId)
                .eventId(UUID.randomUUID())
                .eventType(Reminder.EventType.APPOINTMENT)
                .title("Updated Reminder")
                .description("Updated Description")
                .reminderTime(LocalDateTime.now().plusHours(1))
                .leadTimeMinutes(45)
                .channel(Reminder.ReminderChannel.PUSH)
                .build();

        when(reminderService.updateReminder(eq(reminderId), any(ReminderUpdateDto.class)))
                .thenReturn(updatedReminder);

        // When & Then
        mockMvc.perform(patch("/api/v1/reminders/{reminderId}", reminderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.leadTimeMinutes").value(45))
                .andExpect(jsonPath("$.channel").value("PUSH"));
    }
}
