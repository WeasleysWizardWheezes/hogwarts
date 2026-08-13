package de.thkoeln.ccq.firemanager.course;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.thkoeln.ccq.firemanager.course.enrollment.CourseEnrollment;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class CourseE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<String> jsonEntity(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    private String extractId(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode idNode = root.get("id");
            if (idNode != null && idNode.isTextual()) {
                return idNode.asText();
            }
            throw new AssertionError("No top-level 'id' field found in response: " + responseBody);
        } catch (AssertionError e) {
            throw e;
        } catch (Exception e) {
            throw new AssertionError("Could not parse response JSON: " + responseBody, e);
        }
    }

    private String createCourseViaApi(String name, String description, int maxParticipants, UUID instructorId, String instructorName) {
        String json = """
                {"name": "%s", "description": "%s", "maxParticipants": %d, 
                 "instructorId": "%s", "instructorName": "%s", 
                 "startDate": "%s", "endDate": "%s", "status": "PLANNED"}
                """.formatted(name, description, maxParticipants, instructorId, instructorName,
                OffsetDateTime.now().toString(), OffsetDateTime.now().plusDays(7).toString());
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/courses"), jsonEntity(json), String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        return extractId(response.getBody());
    }

    @Test
    void createCourse_returnsCreated() {
        // Act
        String json = """
                {"name": "Atemschutzkurs", "description": "Grundausbildung Atemschutz", "maxParticipants": 10, 
                 "instructorId": "550e8400-e29b-41d4-a716-446655440000", "instructorName": "Max Mustermann", 
                 "startDate": "2024-01-01T00:00:00Z", "endDate": "2024-01-08T00:00:00Z", "status": "PLANNED"}
                """;
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/courses"), jsonEntity(json), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("\"name\":\"Atemschutzkurs\"");
    }

    @Test
    void getAllCourses_returnsListAfterCreate() {
        // Arrange
        var instructorId = UUID.randomUUID();
        createCourseViaApi("Kurs 1", "Beschreibung 1", 5, instructorId, "Instructor 1");
        createCourseViaApi("Kurs 2", "Beschreibung 2", 10, instructorId, "Instructor 2");

        // Act
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url("/api/v1/courses"),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void getCourseById_returnsCorrectData() {
        // Arrange
        var instructorId = UUID.randomUUID();
        String courseId = createCourseViaApi("Kurs 1", "Beschreibung 1", 5, instructorId, "Instructor 1");

        // Act
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                url("/api/v1/courses/" + courseId),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().get("name")).isEqualTo("Kurs 1");
    }

    @Test
    void updateCourse_returnsUpdatedData() {
        // Arrange
        var instructorId = UUID.randomUUID();
        String courseId = createCourseViaApi("Alter Kurs", "Alte Beschreibung", 5, instructorId, "Alter Instructor");

        String updateJson = """
                {"name": "Neuer Kurs", "description": "Neue Beschreibung", "maxParticipants": 10, 
                 "instructorId": "%s", "instructorName": "Neuer Instructor", 
                 "startDate": "2024-01-01T00:00:00Z", "endDate": "2024-01-08T00:00:00Z", "status": "PLANNED"}
                """.formatted(instructorId);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/courses/" + courseId),
                HttpMethod.PUT, jsonEntity(updateJson), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("\"name\":\"Neuer Kurs\"");
    }

    @Test
    void deleteCourse_returnsNoContent() {
        // Arrange
        var instructorId = UUID.randomUUID();
        String courseId = createCourseViaApi("Kurs zum Löschen", "Beschreibung", 5, instructorId, "Instructor");

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                url("/api/v1/courses/" + courseId),
                HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    void createEnrollment_returnsCreated() {
        // Arrange
        var instructorId = UUID.randomUUID();
        String courseId = createCourseViaApi("Kurs", "Beschreibung", 5, instructorId, "Instructor");

        String enrollmentJson = """
                {"memberId": "550e8400-e29b-41d4-a716-446655440000", "memberName": "Max Mustermann", "status": "PENDING", "comment": "Anmeldung"}
                """;

        // Act
        ResponseEntity<String> response = restTemplate.postForEntity(
                url("/api/v1/courses/" + courseId + "/enrollments"), jsonEntity(enrollmentJson), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void getEnrollmentsByCourse_returnsList() {
        // Arrange
        var instructorId = UUID.randomUUID();
        String courseId = createCourseViaApi("Kurs", "Beschreibung", 5, instructorId, "Instructor");

        String enrollmentJson1 = """
                {"memberId": "550e8400-e29b-41d4-a716-446655440000", "memberName": "Max Mustermann", "status": "PENDING", "comment": "Anmeldung 1"}
                """;
        restTemplate.postForEntity(
                url("/api/v1/courses/" + courseId + "/enrollments"), jsonEntity(enrollmentJson1), String.class);

        String enrollmentJson2 = """
                {"memberId": "650e8400-e29b-41d4-a716-446655440000", "memberName": "Andreas Müller", "status": "CONFIRMED", "comment": "Anmeldung 2"}
                """;
        restTemplate.postForEntity(
                url("/api/v1/courses/" + courseId + "/enrollments"), jsonEntity(enrollmentJson2), String.class);

        // Act
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url("/api/v1/courses/" + courseId + "/enrollments"),
                HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {});

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    @Test
    void cancelEnrollment_returnsNoContent() {
        // Arrange
        var instructorId = UUID.randomUUID();
        String courseId = createCourseViaApi("Kurs", "Beschreibung", 5, instructorId, "Instructor");

        String enrollmentJson = """
                {"memberId": "550e8400-e29b-41d4-a716-446655440000", "memberName": "Max Mustermann", "status": "PENDING", "comment": "Anmeldung"}
                """;
        ResponseEntity<String> createResponse = restTemplate.postForEntity(
                url("/api/v1/courses/" + courseId + "/enrollments"), jsonEntity(enrollmentJson), String.class);
        String enrollmentId = extractId(createResponse.getBody());

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                url("/api/v1/courses/" + courseId + "/enrollments/" + enrollmentId),
                HttpMethod.DELETE, null, Void.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}