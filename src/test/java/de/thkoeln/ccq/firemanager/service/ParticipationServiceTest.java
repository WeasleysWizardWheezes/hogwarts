package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.model.Appointment;
import de.thkoeln.ccq.firemanager.model.User;
import de.thkoeln.ccq.firemanager.repository.AppointmentRepository;
import de.thkoeln.ccq.firemanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipationServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ParticipationService participationService;

    private Appointment appointment;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        appointment = Appointment.builder()
                .id("app1")
                .title("Test Appointment")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .participants(new HashSet<>())
                .build();

        user1 = User.builder()
                .id("user1")
                .username("user1")
                .email("user1@example.com")
                .firstName("User")
                .lastName("One")
                .build();

        user2 = User.builder()
                .id("user2")
                .username("user2")
                .email("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .build();
    }

    @Test
    void addParticipants() {
        List<String> userIds = Arrays.asList("user1", "user2");

        when(appointmentRepository.findById("app1")).thenReturn(Optional.of(appointment));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user2")).thenReturn(Optional.of(user2));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        assertDoesNotThrow(() -> {
            participationService.addParticipants("app1", userIds);
        });

        verify(appointmentRepository, times(1)).findById("app1");
        verify(userRepository, times(1)).findById("user1");
        verify(userRepository, times(1)).findById("user2");
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void addParticipants_AppointmentNotFound() {
        List<String> userIds = Arrays.asList("user1", "user2");

        when(appointmentRepository.findById("app1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            participationService.addParticipants("app1", userIds);
        });

        verify(appointmentRepository, times(1)).findById("app1");
        verify(userRepository, never()).findById(anyString());
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void addParticipants_UserNotFound() {
        List<String> userIds = Arrays.asList("user1", "user2");

        when(appointmentRepository.findById("app1")).thenReturn(Optional.of(appointment));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user2")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            participationService.addParticipants("app1", userIds);
        });

        verify(appointmentRepository, times(1)).findById("app1");
        verify(userRepository, times(1)).findById("user1");
        verify(userRepository, times(1)).findById("user2");
        verify(appointmentRepository, never()).save(any(Appointment.class));
    }

    @Test
    void removeParticipants() {
        List<String> userIds = Arrays.asList("user1", "user2");
        appointment.getParticipants().addAll(Arrays.asList(user1, user2));

        when(appointmentRepository.findById("app1")).thenReturn(Optional.of(appointment));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user2")).thenReturn(Optional.of(user2));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        assertDoesNotThrow(() -> {
            participationService.removeParticipants("app1", userIds);
        });

        verify(appointmentRepository, times(1)).findById("app1");
        verify(userRepository, times(1)).findById("user1");
        verify(userRepository, times(1)).findById("user2");
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void acceptParticipation() {
        when(appointmentRepository.findById("app1")).thenReturn(Optional.of(appointment));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        assertDoesNotThrow(() -> {
            participationService.acceptParticipation("app1", "user1");
        });

        verify(appointmentRepository, times(1)).findById("app1");
        verify(userRepository, times(1)).findById("user1");
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void declineParticipation() {
        appointment.getParticipants().add(user1);

        when(appointmentRepository.findById("app1")).thenReturn(Optional.of(appointment));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

        assertDoesNotThrow(() -> {
            participationService.declineParticipation("app1", "user1");
        });

        verify(appointmentRepository, times(1)).findById("app1");
        verify(userRepository, times(1)).findById("user1");
        verify(appointmentRepository, times(1)).save(any(Appointment.class));
    }

    @Test
    void getParticipationStatus() {
        when(appointmentRepository.findById("app1")).thenReturn(Optional.of(appointment));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));

        String status = participationService.getParticipationStatus("app1", "user1");

        assertNotNull(status);
        assertEquals("UNDECIDED", status);

        verify(appointmentRepository, times(1)).findById("app1");
        verify(userRepository, times(1)).findById("user1");
    }

    @Test
    void getParticipationStatus_Accepted() {
        appointment.getParticipants().add(user1);

        when(appointmentRepository.findById("app1")).thenReturn(Optional.of(appointment));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));

        String status = participationService.getParticipationStatus("app1", "user1");

        assertNotNull(status);
        assertEquals("ACCEPTED", status);

        verify(appointmentRepository, times(1)).findById("app1");
        verify(userRepository, times(1)).findById("user1");
    }
}
