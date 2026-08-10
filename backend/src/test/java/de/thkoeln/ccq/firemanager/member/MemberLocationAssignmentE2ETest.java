package de.thkoeln.ccq.firemanager.member;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("e2e")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MemberLocationAssignmentE2ETest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @LocalServerPort
    private int port;

    private final RestTemplate restTemplate = new RestTemplate() {{
        setErrorHandler(new org.springframework.web.client.ResponseErrorHandler() {
            @Override
            public boolean hasError(org.springframework.http.client.ClientHttpResponse response) throws java.io.IOException {
                return false;
            }

            @Override
            public void handleError(org.springframework.http.client.ClientHttpResponse response) throws java.io.IOException {
                // Do nothing
            }
        });
    }};

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<String> jsonEntity(String json) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(json, headers);
    }

    @Test
    void assignMemberToLocation_returnsOk() {
        // Arrange - Create a location first
        String locationJson = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;
        ResponseEntity<String> locationResponse = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(locationJson), String.class);
        
        String locationId = extractIdFromResponse(locationResponse.getBody());
        UUID memberId = UUID.randomUUID();

        String assignmentJson = """
            {
                "locationId": "%s"
            }
            """.formatted(locationId);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/members/" + memberId + "/locations"),
                HttpMethod.POST,
                jsonEntity(assignmentJson),
                String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(memberId.toString());
        assertThat(response.getBody()).contains(locationId);
    }

    @Test
    void assignMemberToLocation_returnsBadRequestWhenLocationIdIsInvalid() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        String assignmentJson = """
            {
                "locationId": "invalid-uuid"
            }
            """;

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/members/" + memberId + "/locations"),
                HttpMethod.POST,
                jsonEntity(assignmentJson),
                String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        // Just verify that we get a response body (the exact content may vary)
        assertThat(response.getBody()).isNotNull();
    }

    @Test
    void getMember_returnsOkWhenMemberHasAssignment() {
        // Arrange - Create a location and assign member
        String locationJson = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;
        ResponseEntity<String> locationResponse = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(locationJson), String.class);
        
        String locationId = extractIdFromResponse(locationResponse.getBody());
        UUID memberId = UUID.randomUUID();

        String assignmentJson = """
            {
                "locationId": "%s"
            }
            """.formatted(locationId);
        
        restTemplate.exchange(
                url("/api/v1/members/" + memberId + "/locations"),
                HttpMethod.POST,
                jsonEntity(assignmentJson),
                String.class);

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/members/" + memberId), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(memberId.toString());
        assertThat(response.getBody()).contains(locationId);
    }

    @Test
    void getMember_returnsNotFoundWhenMemberHasNoAssignment() {
        // Arrange
        UUID nonExistentMemberId = UUID.randomUUID();

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/members/" + nonExistentMemberId), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        // The implementation returns no body for 404, which is acceptable
    }

    @Test
    void listMembers_returnsEmptyListWhenNoMembersExist() {
        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/members"), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("[]");
    }

    @Test
    void listMembers_returnsAllMembers() {
        // Arrange - Create a location and assign two members
        String locationJson = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;
        ResponseEntity<String> locationResponse = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(locationJson), String.class);
        
        String locationId = extractIdFromResponse(locationResponse.getBody());
        UUID memberId1 = UUID.randomUUID();
        UUID memberId2 = UUID.randomUUID();

        String assignmentJson1 = """
            {
                "locationId": "%s"
            }
            """.formatted(locationId);
        
        String assignmentJson2 = """
            {
                "locationId": "%s"
            }
            """.formatted(locationId);
        
        restTemplate.exchange(
                url("/api/v1/members/" + memberId1 + "/locations"),
                HttpMethod.POST,
                jsonEntity(assignmentJson1),
                String.class);
        
        restTemplate.exchange(
                url("/api/v1/members/" + memberId2 + "/locations"),
                HttpMethod.POST,
                jsonEntity(assignmentJson2),
                String.class);

        // Act
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/members"), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(memberId1.toString());
        assertThat(response.getBody()).contains(memberId2.toString());
    }

    @Test
    void listMembers_returnsFilteredMembersByLocation() {
        // Arrange - Create two locations and assign members
        String locationJson1 = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;
        String locationJson2 = """
            {
                "name": "Gerätehaus Bonn",
                "address": "Beispielweg 2, 53111 Bonn",
                "type": "FIRE_STATION"
            }
            """;
        
        ResponseEntity<String> locationResponse1 = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(locationJson1), String.class);
        ResponseEntity<String> locationResponse2 = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(locationJson2), String.class);
        
        String locationId1 = extractIdFromResponse(locationResponse1.getBody());
        String locationId2 = extractIdFromResponse(locationResponse2.getBody());
        
        UUID memberId1 = UUID.randomUUID();
        UUID memberId2 = UUID.randomUUID();

        String assignmentJson1 = """
            {
                "locationId": "%s"
            }
            """.formatted(locationId1);
        
        String assignmentJson2 = """
            {
                "locationId": "%s"
            }
            """.formatted(locationId2);
        
        restTemplate.exchange(
                url("/api/v1/members/" + memberId1 + "/locations"),
                HttpMethod.POST,
                jsonEntity(assignmentJson1),
                String.class);
        
        restTemplate.exchange(
                url("/api/v1/members/" + memberId2 + "/locations"),
                HttpMethod.POST,
                jsonEntity(assignmentJson2),
                String.class);

        // Act - Filter by locationId1
        ResponseEntity<String> response = restTemplate.getForEntity(
                url("/api/v1/members?locationId=" + locationId1), String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains(memberId1.toString());
        assertThat(response.getBody()).doesNotContain(memberId2.toString());
    }

    @Test
    void deleteMemberLocationAssignment_returnsMethodNotAllowed() {
        // Arrange - Create a location and assign member
        String locationJson = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;
        ResponseEntity<String> locationResponse = restTemplate.postForEntity(
                url("/api/v1/locations"), jsonEntity(locationJson), String.class);
        
        String locationId = extractIdFromResponse(locationResponse.getBody());
        UUID memberId = UUID.randomUUID();

        String assignmentJson = """
            {
                "locationId": "%s"
            }
            """.formatted(locationId);
        
        restTemplate.exchange(
                url("/api/v1/members/" + memberId + "/locations"),
                HttpMethod.POST,
                jsonEntity(assignmentJson),
                String.class);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                url("/api/v1/members/" + memberId + "/locations"),
                HttpMethod.DELETE,
                null,
                String.class);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.METHOD_NOT_ALLOWED);
        assertThat(response.getBody()).contains("Method 'DELETE' is not supported");
    }

    private String extractIdFromResponse(String responseBody) {
        // Simple extraction of UUID from JSON response
        int start = responseBody.indexOf("\"id\":");
        if (start == -1) {
            throw new IllegalStateException("Cannot extract ID from response: " + responseBody);
        }
        start += 6; // Skip "id":"
        int end = responseBody.indexOf("\"", start);
        return responseBody.substring(start, end);
    }
}