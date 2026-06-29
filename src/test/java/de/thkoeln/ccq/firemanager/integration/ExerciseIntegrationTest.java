package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.ExerciseDTO;
import de.thkoeln.ccq.firemanager.model.Exercise;
import de.thkoeln.ccq.firemanager.repository.ExerciseRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExerciseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Test
    void testCreateAndGetExercise() {
        // Create exercise
        ExerciseDTO createDTO = ExerciseDTO.builder()
                .title("Test Exercise")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ExerciseDTO> request = new HttpEntity<>(createDTO, headers);

        ResponseEntity<ExerciseDTO> createResponse = restTemplate.postForEntity(
                "/api/exercises", request, ExerciseDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        // Get exercise by ID
        String id = createResponse.getBody().getId();
        ResponseEntity<ExerciseDTO> getResponse = restTemplate.getForEntity(
                "/api/exercises/" + id, ExerciseDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
        assertEquals("Test Exercise", getResponse.getBody().getTitle());

        // Clean up
        exerciseRepository.deleteById(id);
    }

    @Test
    void testGetAllExercises() {
        // Create test data
        Exercise exercise1 = Exercise.builder()
                .title("Exercise 1")
                .description("Description 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();
        Exercise exercise2 = Exercise.builder()
                .title("Exercise 2")
                .description("Description 2")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(1).plusHours(2))
                .isRecurring(true)
                .recurrencePattern("WEEKLY")
                .build();

        exerciseRepository.saveAll(Arrays.asList(exercise1, exercise2));

        // Get all exercises
        ResponseEntity<ExerciseDTO[]> response = restTemplate.getForEntity(
                "/api/exercises", ExerciseDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);

        // Clean up
        exerciseRepository.deleteAll();
    }
}
