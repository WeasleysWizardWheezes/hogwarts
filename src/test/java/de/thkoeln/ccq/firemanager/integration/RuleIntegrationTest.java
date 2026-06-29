package de.thkoeln.ccq.firemanager.integration;

import de.thkoeln.ccq.firemanager.dto.RuleDTO;
import de.thkoeln.ccq.firemanager.model.Rule;
import de.thkoeln.ccq.firemanager.repository.RuleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RuleIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RuleRepository ruleRepository;

    @Test
    void testCreateAndGetRule() {
        // Create rule
        RuleDTO createDTO = RuleDTO.builder()
                .name("Test Rule")
                .description("Test Description")
                .conditions("condition1")
                .actions("action1")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RuleDTO> request = new HttpEntity<>(createDTO, headers);

        ResponseEntity<RuleDTO> createResponse = restTemplate.postForEntity(
                "/api/rules", request, RuleDTO.class);

        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getId());

        // Get rule by ID
        String id = createResponse.getBody().getId();
        ResponseEntity<RuleDTO> getResponse = restTemplate.getForEntity(
                "/api/rules/" + id, RuleDTO.class);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals(id, getResponse.getBody().getId());
        assertEquals("Test Rule", getResponse.getBody().getName());

        // Clean up
        ruleRepository.deleteById(id);
    }

    @Test
    void testGetAllRules() {
        // Create test data
        Rule rule1 = Rule.builder()
                .name("Rule 1")
                .description("Description 1")
                .conditions("condition1")
                .actions("action1")
                .build();
        Rule rule2 = Rule.builder()
                .name("Rule 2")
                .description("Description 2")
                .conditions("condition2")
                .actions("action2")
                .build();

        ruleRepository.saveAll(Arrays.asList(rule1, rule2));

        // Get all rules
        ResponseEntity<RuleDTO[]> response = restTemplate.getForEntity(
                "/api/rules", RuleDTO[].class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length >= 2);

        // Clean up
        ruleRepository.deleteAll();
    }
}
