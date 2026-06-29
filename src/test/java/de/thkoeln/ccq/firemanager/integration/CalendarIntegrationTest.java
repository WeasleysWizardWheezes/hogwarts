package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.CalendarDTO;
import de.thkoeln.ccq.firemanager.model.Calendar;
import de.thkoeln.ccq.firemanager.repository.CalendarRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CalendarIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CalendarRepository calendarRepository;

    @Test
    void testCreateAndGetCalendar() {
        // Create calendar
        CalendarDTO createDTO = CalendarDTO.builder()
                .name("Test Calendar")
                .description("Test Description")
                .isPublic(true)
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CalendarDTO> request = new HttpEntity<>(createDTO, headers);

        ResponseEntity<CalendarDTO> createResponse = restTemplate.postForEntity(
                "/api/calendars", request, CalendarDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        // Get calendar by ID
        String id = createResponse.getBody().getId();
        ResponseEntity<CalendarDTO> getResponse = restTemplate.getForEntity(
                "/api/calendars/" + id, CalendarDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
        assertEquals("Test Calendar", getResponse.getBody().getName());

        // Clean up
        calendarRepository.deleteById(id);
    }

    @Test
    void testGetAllCalendars() {
        // Create test data
        Calendar calendar1 = Calendar.builder()
                .name("Calendar 1")
                .description("Description 1")
                .isPublic(true)
                .build();
        Calendar calendar2 = Calendar.builder()
                .name("Calendar 2")
                .description("Description 2")
                .isPublic(false)
                .build();

        calendarRepository.saveAll(Arrays.asList(calendar1, calendar2));

        // Get all calendars
        ResponseEntity<CalendarDTO[]> response = restTemplate.getForEntity(
                "/api/calendars", CalendarDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);

        // Clean up
        calendarRepository.deleteAll();
    }
}
