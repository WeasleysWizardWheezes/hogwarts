package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.AppointmentDTO;
import de.thkoeln.ccq.firemanager.service.AppointmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentControllerTest {

    @Mock
    private AppointmentService appointmentService;

    @InjectMocks
    private AppointmentController appointmentController;

    private AppointmentDTO appointmentDTO;

    @BeforeEach
    void setUp() {
        appointmentDTO = AppointmentDTO.builder()
                .id("1")
                .title("Test Appointment")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .organizationUnitId("org1")
                .calendarId("cal1")
                .build();
    }

    @Test
    void createAppointment() {
        when(appointmentService.createAppointment(any(AppointmentDTO.class))).thenReturn(appointmentDTO);

        ResponseEntity<AppointmentDTO> response = appointmentController.createAppointment(appointmentDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(appointmentDTO, response.getBody());
    }

    @Test
    void getAppointmentById() {
        when(appointmentService.getAppointmentById(anyString())).thenReturn(appointmentDTO);

        ResponseEntity<AppointmentDTO> response = appointmentController.getAppointmentById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDTO, response.getBody());
    }

    @Test
    void getAllAppointments() {
        List<AppointmentDTO> appointments = Arrays.asList(appointmentDTO);
        when(appointmentService.getAllAppointments()).thenReturn(appointments);

        ResponseEntity<List<AppointmentDTO>> response = appointmentController.getAllAppointments();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAppointmentsByOrganizationUnit() {
        List<AppointmentDTO> appointments = Arrays.asList(appointmentDTO);
        when(appointmentService.getAppointmentsByOrganizationUnit(anyString())).thenReturn(appointments);

        ResponseEntity<List<AppointmentDTO>> response = appointmentController.getAppointmentsByOrganizationUnit("org1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAppointmentsByCalendar() {
        List<AppointmentDTO> appointments = Arrays.asList(appointmentDTO);
        when(appointmentService.getAppointmentsByCalendar(anyString())).thenReturn(appointments);

        ResponseEntity<List<AppointmentDTO>> response = appointmentController.getAppointmentsByCalendar("cal1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateAppointment() {
        when(appointmentService.updateAppointment(anyString(), any(AppointmentDTO.class))).thenReturn(appointmentDTO);

        ResponseEntity<AppointmentDTO> response = appointmentController.updateAppointment("1", appointmentDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(appointmentDTO, response.getBody());
    }

    @Test
    void deleteAppointment() {
        doNothing().when(appointmentService).deleteAppointment(anyString());

        ResponseEntity<Void> response = appointmentController.deleteAppointment("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
