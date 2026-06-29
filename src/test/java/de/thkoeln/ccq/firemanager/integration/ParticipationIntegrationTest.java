package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.AppointmentDTO;
import de.thkoeln.ccq.firemanager.dto.UserDTO;
import de.thkoeln.ccq.firemanager.model.Appointment;
import de.thkoeln.ccq.firemanager.model.User;
import de.thkoeln.ccq.firemanager.repository.AppointmentRepository;
import de.thkoeln.ccq.firemanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParticipationIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testAddParticipants() {
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

        // Create users
        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .firstName("User")
                .lastName("One")
                .build();
        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .build();
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        // Add participants
        List<String> userIds = Arrays.asList(user1.getId(), user2.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> request = new HttpEntity<>(userIds, headers);

        ResponseEntity<Void> addResponse = restTemplate.exchange(
                "/api/appointments/" + appointment.getId() + "/participants",
                HttpMethod.POST,
                request,
                Void.class);

        assertEquals(HttpStatus.OK, addResponse.getStatusCode());

        // Clean up
        appointmentRepository.deleteById(appointment.getId());
        userRepository.deleteById(user1.getId());
        userRepository.deleteById(user2.getId());
    }

    @Test
    void testRemoveParticipants() {
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

        // Create users
        User user1 = User.builder()
                .username("user1")
                .email("user1@example.com")
                .firstName("User")
                .lastName("One")
                .build();
        User user2 = User.builder()
                .username("user2")
                .email("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .build();
        user1 = userRepository.save(user1);
        user2 = userRepository.save(user2);

        // Remove participants
        List<String> userIds = Arrays.asList(user1.getId(), user2.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<List<String>> request = new HttpEntity<>(userIds, headers);

        ResponseEntity<Void> removeResponse = restTemplate.exchange(
                "/api/appointments/" + appointment.getId() + "/participants",
                HttpMethod.DELETE,
                request,
                Void.class);

        assertEquals(HttpStatus.OK, removeResponse.getStatusCode());

        // Clean up
        appointmentRepository.deleteById(appointment.getId());
        userRepository.deleteById(user1.getId());
        userRepository.deleteById(user2.getId());
    }

    @Test
    void testAcceptParticipation() {
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

        // Create user
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
        user = userRepository.save(user);

        // Accept participation
        ResponseEntity<Void> acceptResponse = restTemplate.exchange(
                "/api/appointments/" + appointment.getId() + "/participants/" + user.getId() + "/accept",
                HttpMethod.POST,
                null,
                Void.class);

        assertEquals(HttpStatus.OK, acceptResponse.getStatusCode());

        // Clean up
        appointmentRepository.deleteById(appointment.getId());
        userRepository.deleteById(user.getId());
    }

    @Test
    void testDeclineParticipation() {
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

        // Create user
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
        user = userRepository.save(user);

        // Decline participation
        ResponseEntity<Void> declineResponse = restTemplate.exchange(
                "/api/appointments/" + appointment.getId() + "/participants/" + user.getId() + "/decline",
                HttpMethod.POST,
                null,
                Void.class);

        assertEquals(HttpStatus.OK, declineResponse.getStatusCode());

        // Clean up
        appointmentRepository.deleteById(appointment.getId());
        userRepository.deleteById(user.getId());
    }

    @Test
    void testGetParticipationStatus() {
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

        // Create user
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();
        user = userRepository.save(user);

        // Get participation status
        ResponseEntity<String> statusResponse = restTemplate.exchange(
                "/api/appointments/" + appointment.getId() + "/participants/" + user.getId() + "/status",
                HttpMethod.GET,
                null,
                String.class);

        assertEquals(HttpStatus.OK, statusResponse.getStatusCode());
        assertNotNull(statusResponse.getBody());

        // Clean up
        appointmentRepository.deleteById(appointment.getId());
        userRepository.deleteById(user.getId());
    }
}
