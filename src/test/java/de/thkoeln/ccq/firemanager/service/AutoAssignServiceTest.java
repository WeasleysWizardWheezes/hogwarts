package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.model.Appointment;
import de.thkoeln.ccq.firemanager.model.Rule;
import de.thkoeln.ccq.firemanager.repository.AppointmentRepository;
import de.thkoeln.ccq.firemanager.repository.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AutoAssignServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private RuleRepository ruleRepository;

    @InjectMocks
    private AutoAssignService autoAssignService;

    private Appointment appointment;
    private Rule rule;

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
                .build();

        rule = Rule.builder()
                .id("rule1")
                .name("Test Rule")
                .description("Test Description")
                .conditions("condition1")
                .actions("action1")
                .build();
    }

    @Test
    void autoAssign() {
        when(appointmentRepository.findById("app1")).thenReturn(Optional.of(appointment));
        when(ruleRepository.findById("rule1")).thenReturn(Optional.of(rule));

        assertDoesNotThrow(() -> {
            autoAssignService.autoAssign("app1", "rule1");
        });

        verify(appointmentRepository, times(1)).findById("app1");
        verify(ruleRepository, times(1)).findById("rule1");
    }

    @Test
    void autoAssign_AppointmentNotFound() {
        when(appointmentRepository.findById("app1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            autoAssignService.autoAssign("app1", "rule1");
        });

        verify(appointmentRepository, times(1)).findById("app1");
        verify(ruleRepository, never()).findById(anyString());
    }

    @Test
    void autoAssign_RuleNotFound() {
        when(appointmentRepository.findById("app1")).thenReturn(Optional.of(appointment));
        when(ruleRepository.findById("rule1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            autoAssignService.autoAssign("app1", "rule1");
        });

        verify(appointmentRepository, times(1)).findById("app1");
        verify(ruleRepository, times(1)).findById("rule1");
    }
}
