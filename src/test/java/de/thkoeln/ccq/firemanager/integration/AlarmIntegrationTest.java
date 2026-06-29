package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.AlarmDTO;
import de.thkoeln.ccq.firemanager.model.Alarm;
import de.thkoeln.ccq.firemanager.repository.AlarmRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AlarmIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private AlarmRepository alarmRepository;

    @Test
    void testCreateAndGetAlarm() {
        // Create alarm
        AlarmDTO createDTO = AlarmDTO.builder()
                .title("Test Alarm")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AlarmDTO> request = new HttpEntity<>(createDTO, headers);

        ResponseEntity<AlarmDTO> createResponse = restTemplate.postForEntity(
                "/api/alarms", request, AlarmDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        // Get alarm by ID
        String id = createResponse.getBody().getId();
        ResponseEntity<AlarmDTO> getResponse = restTemplate.getForEntity(
                "/api/alarms/" + id, AlarmDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
        assertEquals("Test Alarm", getResponse.getBody().getTitle());

        // Clean up
        alarmRepository.deleteById(id);
    }

    @Test
    void testGetAllAlarms() {
        // Create test data
        Alarm alarm1 = Alarm.builder()
                .title("Alarm 1")
                .description("Description 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();
        Alarm alarm2 = Alarm.builder()
                .title("Alarm 2")
                .description("Description 2")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(1).plusHours(2))
                .isRecurring(true)
                .recurrencePattern("WEEKLY")
                .build();

        alarmRepository.saveAll(Arrays.asList(alarm1, alarm2));

        // Get all alarms
        ResponseEntity<AlarmDTO[]> response = restTemplate.getForEntity(
                "/api/alarms", AlarmDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);

        // Clean up
        alarmRepository.deleteAll();
    }
}
