package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.CalendarDTO;
import de.thkoeln.ccq.firemanager.dto.CalendarShareRequestDTO;
import de.thkoeln.ccq.firemanager.model.Calendar;
import de.thkoeln.ccq.firemanager.model.User;
import de.thkoeln.ccq.firemanager.repository.CalendarRepository;
import de.thkoeln.ccq.firemanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CalendarSharingIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CalendarRepository calendarRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testShareCalendar() {
        // Create calendar
        Calendar calendar = Calendar.builder()
                .name("Test Calendar")
                .description("Test Description")
                .isPublic(false)
                .build();
        calendar = calendarRepository.save(calendar);

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

        // Share calendar
        CalendarShareRequestDTO shareRequest = CalendarShareRequestDTO.builder()
                .userIds(Arrays.asList(user1.getId(), user2.getId()))
                .permissions(Arrays.asList("READ", "WRITE"))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<CalendarShareRequestDTO> request = new HttpEntity<>(shareRequest, headers);

        ResponseEntity<Void> shareResponse = restTemplate.exchange(
                "/api/calendars/" + calendar.getId() + "/share",
                HttpMethod.POST,
                request,
                Void.class);

        assertEquals(HttpStatus.OK, shareResponse.getStatusCode());

        // Clean up
        calendarRepository.deleteById(calendar.getId());
        userRepository.deleteById(user1.getId());
        userRepository.deleteById(user2.getId());
    }
}
