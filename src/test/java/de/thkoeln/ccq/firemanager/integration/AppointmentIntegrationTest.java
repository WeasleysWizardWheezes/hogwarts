package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.AppointmentDTO;
import de.thkoeln.ccq.firemanager.model.Appointment;
import de.thkoeln.ccq.firemanager.repository.AppointmentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AppointmentIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Test
    void testCreateAndGetAppointment() {
        // Create appointment
        AppointmentDTO createDTO = AppointmentDTO.builder()
                .title("Test Appointment")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AppointmentDTO> request = new HttpEntity<>(createDTO, headers);

        ResponseEntity<AppointmentDTO> createResponse = restTemplate.postForEntity(
                "/api/appointments", request, AppointmentDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        // Get appointment by ID
        String id = createResponse.getBody().getId();
        ResponseEntity<AppointmentDTO> getResponse = restTemplate.getForEntity(
                "/api/appointments/" + id, AppointmentDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
        assertEquals("Test Appointment", getResponse.getBody().getTitle());

        // Clean up
        appointmentRepository.deleteById(id);
    }

    @Test
    void testGetAllAppointments() {
        // Create test data
        Appointment appointment1 = Appointment.builder()
                .title("Appointment 1")
                .description("Description 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();
        Appointment appointment2 = Appointment.builder()
                .title("Appointment 2")
                .description("Description 2")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(1).plusHours(2))
                .isRecurring(true)
                .recurrencePattern("WEEKLY")
                .build();

        appointmentRepository.saveAll(Arrays.asList(appointment1, appointment2));

        // Get all appointments
        ResponseEntity<AppointmentDTO[]> response = restTemplate.getForEntity(
                "/api/appointments", AppointmentDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);

        // Clean up
        appointmentRepository.deleteAll();
    }
}
