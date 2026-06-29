package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.AppointmentDTO;
import de.thkoeln.ccq.firemanager.model.Appointment;
import de.thkoeln.ccq.firemanager.repository.AppointmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    private AppointmentDTO appointmentDTO;
    private Appointment appointment;

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

        appointment = Appointment.builder()
                .id("1")
                .title("Test Appointment")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();
    }

    @Test
    void createAppointment() {
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDTO result = appointmentService.createAppointment(appointmentDTO);

        assertNotNull(result);
        assertEquals(appointmentDTO.getId(), result.getId());
        assertEquals(appointmentDTO.getTitle(), result.getTitle());
        assertEquals(appointmentDTO.getStartDate(), result.getStartDate());
    }

    @Test
    void getAppointmentById() {
        when(appointmentRepository.findById("1")).thenReturn(Optional.of(appointment));

        AppointmentDTO result = appointmentService.getAppointmentById("1");

        assertNotNull(result);
        assertEquals(appointment.getId(), result.getId());
        assertEquals(appointment.getTitle(), result.getTitle());
    }

    @Test
    void getAppointmentById_NotFound() {
        when(appointmentRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            appointmentService.getAppointmentById("1");
        });
    }

    @Test
    void getAllAppointments() {
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findAll()).thenReturn(appointments);

        List<AppointmentDTO> result = appointmentService.getAllAppointments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointment.getId(), result.get(0).getId());
    }

    @Test
    void getAppointmentsByOrganizationUnit() {
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findByOrganizationUnitId("org1")).thenReturn(appointments);

        List<AppointmentDTO> result = appointmentService.getAppointmentsByOrganizationUnit("org1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointment.getId(), result.get(0).getId());
    }

    @Test
    void getAppointmentsByCalendar() {
        List<Appointment> appointments = Arrays.asList(appointment);
        when(appointmentRepository.findByCalendarId("cal1")).thenReturn(appointments);

        List<AppointmentDTO> result = appointmentService.getAppointmentsByCalendar("cal1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(appointment.getId(), result.get(0).getId());
    }

    @Test
    void updateAppointment() {
        when(appointmentRepository.findById("1")).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        AppointmentDTO result = appointmentService.updateAppointment("1", appointmentDTO);

        assertNotNull(result);
        assertEquals(appointmentDTO.getId(), result.getId());
        assertEquals(appointmentDTO.getTitle(), result.getTitle());
    }

    @Test
    void updateAppointment_NotFound() {
        when(appointmentRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            appointmentService.updateAppointment("1", appointmentDTO);
        });
    }

    @Test
    void deleteAppointment() {
        doNothing().when(appointmentRepository).deleteById("1");

        assertDoesNotThrow(() -> {
            appointmentService.deleteAppointment("1");
        });

        verify(appointmentRepository, times(1)).deleteById("1");
    }
}
