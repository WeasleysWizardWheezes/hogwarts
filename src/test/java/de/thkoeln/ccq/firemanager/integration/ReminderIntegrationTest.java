package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.ReminderDTO;
import de.thkoeln.ccq.firemanager.model.Reminder;
import de.thkoeln.ccq.firemanager.repository.ReminderRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReminderIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ReminderRepository reminderRepository;

    @Test
    void testCreateAndGetReminder() {
        // Create reminder
        ReminderDTO createDTO = ReminderDTO.builder()
                .appointmentId("app1")
                .userId("user1")
                .timeBefore("PT1H")
                .method("EMAIL")
                .isRecurring(false)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ReminderDTO> request = new HttpEntity<>(createDTO, headers);

        ResponseEntity<ReminderDTO> createResponse = restTemplate.postForEntity(
                "/api/reminders", request, ReminderDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        // Get reminder by ID
        String id = createResponse.getBody().getId();
        ResponseEntity<ReminderDTO> getResponse = restTemplate.getForEntity(
                "/api/reminders/" + id, ReminderDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
        assertEquals("PT1H", getResponse.getBody().getTimeBefore());

        // Clean up
        reminderRepository.deleteById(id);
    }

    @Test
    void testGetAllReminders() {
        // Create test data
        Reminder reminder1 = Reminder.builder()
                .timeBefore("PT1H")
                .method("EMAIL")
                .isRecurring(false)
                .build();
        Reminder reminder2 = Reminder.builder()
                .timeBefore("PT24H")
                .method("SMS")
                .isRecurring(true)
                .build();

        reminderRepository.saveAll(Arrays.asList(reminder1, reminder2));

        // Get all reminders
        ResponseEntity<ReminderDTO[]> response = restTemplate.getForEntity(
                "/api/reminders", ReminderDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);

        // Clean up
        reminderRepository.deleteAll();
    }
}
