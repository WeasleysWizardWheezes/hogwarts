package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.ServiceDTO;
import de.thkoeln.ccq.firemanager.model.Service;
import de.thkoeln.ccq.firemanager.repository.ServiceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ServiceIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ServiceRepository serviceRepository;

    @Test
    void testCreateAndGetService() {
        // Create service
        ServiceDTO createDTO = ServiceDTO.builder()
                .title("Test Service")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ServiceDTO> request = new HttpEntity<>(createDTO, headers);

        ResponseEntity<ServiceDTO> createResponse = restTemplate.postForEntity(
                "/api/services", request, ServiceDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        // Get service by ID
        String id = createResponse.getBody().getId();
        ResponseEntity<ServiceDTO> getResponse = restTemplate.getForEntity(
                "/api/services/" + id, ServiceDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
        assertEquals("Test Service", getResponse.getBody().getTitle());

        // Clean up
        serviceRepository.deleteById(id);
    }

    @Test
    void testGetAllServices() {
        // Create test data
        Service service1 = Service.builder()
                .title("Service 1")
                .description("Description 1")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();
        Service service2 = Service.builder()
                .title("Service 2")
                .description("Description 2")
                .startDate(LocalDateTime.now().plusDays(1))
                .endDate(LocalDateTime.now().plusDays(1).plusHours(2))
                .isRecurring(true)
                .recurrencePattern("WEEKLY")
                .build();

        serviceRepository.saveAll(Arrays.asList(service1, service2));

        // Get all services
        ResponseEntity<ServiceDTO[]> response = restTemplate.getForEntity(
                "/api/services", ServiceDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);

        // Clean up
        serviceRepository.deleteAll();
    }
}
