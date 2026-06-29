package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.UserDTO;
import de.thkoeln.ccq.firemanager.model.User;
import de.thkoeln.ccq.firemanager.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private UserRepository userRepository;

    @Test
    void testCreateAndGetUser() {
        // Create user
        UserDTO createDTO = UserDTO.builder()
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<UserDTO> request = new HttpEntity<>(createDTO, headers);

        ResponseEntity<UserDTO> createResponse = restTemplate.postForEntity(
                "/api/users", request, UserDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        // Get user by ID
        String id = createResponse.getBody().getId();
        ResponseEntity<UserDTO> getResponse = restTemplate.getForEntity(
                "/api/users/" + id, UserDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
        assertEquals("testuser", getResponse.getBody().getUsername());

        // Clean up
        userRepository.deleteById(id);
    }

    @Test
    void testGetAllUsers() {
        // Create test data
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

        userRepository.saveAll(Arrays.asList(user1, user2));

        // Get all users
        ResponseEntity<UserDTO[]> response = restTemplate.getForEntity(
                "/api/users", UserDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);

        // Clean up
        userRepository.deleteAll();
    }
}
