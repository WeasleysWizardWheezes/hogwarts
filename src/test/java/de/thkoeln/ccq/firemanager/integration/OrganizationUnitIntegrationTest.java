package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.OrganizationUnitDTO;
import de.thkoeln.ccq.firemanager.model.OrganizationUnit;
import de.thkoeln.ccq.firemanager.repository.OrganizationUnitRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OrganizationUnitIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrganizationUnitRepository organizationUnitRepository;

    @Test
    void testCreateAndGetOrganizationUnit() {
        // Create organization unit
        OrganizationUnitDTO createDTO = OrganizationUnitDTO.builder()
                .name("Test Unit")
                .description("Test Description")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrganizationUnitDTO> request = new HttpEntity<>(createDTO, headers);

        ResponseEntity<OrganizationUnitDTO> createResponse = restTemplate.postForEntity(
                "/api/organization-units", request, OrganizationUnitDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        // Get organization unit by ID
        String id = createResponse.getBody().getId();
        ResponseEntity<OrganizationUnitDTO> getResponse = restTemplate.getForEntity(
                "/api/organization-units/" + id, OrganizationUnitDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
        assertEquals("Test Unit", getResponse.getBody().getName());

        // Clean up
        organizationUnitRepository.deleteById(id);
    }

    @Test
    void testGetAllOrganizationUnits() {
        // Create test data
        OrganizationUnit unit1 = OrganizationUnit.builder()
                .name("Unit 1")
                .description("Description 1")
                .build();
        OrganizationUnit unit2 = OrganizationUnit.builder()
                .name("Unit 2")
                .description("Description 2")
                .build();

        organizationUnitRepository.saveAll(Arrays.asList(unit1, unit2));

        // Get all organization units
        ResponseEntity<OrganizationUnitDTO[]> response = restTemplate.getForEntity(
                "/api/organization-units", OrganizationUnitDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);

        // Clean up
        organizationUnitRepository.deleteAll();
    }
}
