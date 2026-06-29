package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.AppointmentDTO;
import de.thkoeln.ccq.firemanager.dto.AutoAssignRequestDTO;
import de.thkoeln.ccq.firemanager.dto.RuleDTO;
import de.thkoeln.ccq.firemanager.model.Appointment;
import de.thkoeln.ccq.firemanager.model.Rule;
import de.thkoeln.ccq.firemanager.repository.AppointmentRepository;
import de.thkoeln.ccq.firemanager.repository.RuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AutoAssignIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private RuleRepository ruleRepository;

    @Test
    void testAutoAssign() {
        // Create appointment
        Appointment appointment = Appointment.builder()
                .title("Test Appointment")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();
        appointment = appointmentRepository.save(appointment);

        // Create rule
        Rule rule = Rule.builder()
                .name("Test Rule")
                .description("Test Description")
                .conditions("condition1")
                .actions("action1")
                .build();
        rule = ruleRepository.save(rule);

        // Auto assign
        AutoAssignRequestDTO assignRequest = AutoAssignRequestDTO.builder()
                .appointmentId(appointment.getId())
                .ruleId(rule.getId())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AutoAssignRequestDTO> request = new HttpEntity<>(assignRequest, headers);

        ResponseEntity<Void> assignResponse = restTemplate.exchange(
                "/api/auto-assign",
                HttpMethod.POST,
                request,
                Void.class);

        assertEquals(HttpStatus.OK, assignResponse.getStatusCode());

        // Clean up
        appointmentRepository.deleteById(appointment.getId());
        ruleRepository.deleteById(rule.getId());
    }
}
