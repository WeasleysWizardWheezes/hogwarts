package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.ReminderDTO;
import de.thkoeln.ccq.firemanager.service.ReminderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReminderControllerTest {

    @Mock
    private ReminderService reminderService;

    @InjectMocks
    private ReminderController reminderController;

    private ReminderDTO reminderDTO;

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
    }

    @Test
    void createReminder() {
        when(reminderService.createReminder(any(ReminderDTO.class))).thenReturn(reminderDTO);

        ResponseEntity<ReminderDTO> response = reminderController.createReminder(reminderDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reminderDTO, response.getBody());
    }

    @Test
    void getReminderById() {
        when(reminderService.getReminderById(anyString())).thenReturn(reminderDTO);

        ResponseEntity<ReminderDTO> response = reminderController.getReminderById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reminderDTO, response.getBody());
    }

    @Test
    void getAllReminders() {
        List<ReminderDTO> reminders = Arrays.asList(reminderDTO);
        when(reminderService.getAllReminders()).thenReturn(reminders);

        ResponseEntity<List<ReminderDTO>> response = reminderController.getAllReminders();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getRemindersByAppointment() {
        List<ReminderDTO> reminders = Arrays.asList(reminderDTO);
        when(reminderService.getRemindersByAppointment(anyString())).thenReturn(reminders);

        ResponseEntity<List<ReminderDTO>> response = reminderController.getRemindersByAppointment("app1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getRemindersByUser() {
        List<ReminderDTO> reminders = Arrays.asList(reminderDTO);
        when(reminderService.getRemindersByUser(anyString())).thenReturn(reminders);

        ResponseEntity<List<ReminderDTO>> response = reminderController.getRemindersByUser("user1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateReminder() {
        when(reminderService.updateReminder(anyString(), any(ReminderDTO.class))).thenReturn(reminderDTO);

        ResponseEntity<ReminderDTO> response = reminderController.updateReminder("1", reminderDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reminderDTO, response.getBody());
    }

    @Test
    void deleteReminder() {
        doNothing().when(reminderService).deleteReminder(anyString());

        ResponseEntity<Void> response = reminderController.deleteReminder("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
