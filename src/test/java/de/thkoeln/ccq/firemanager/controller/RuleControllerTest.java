package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.RuleDTO;
import de.thkoeln.ccq.firemanager.service.RuleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleControllerTest {

    @Mock
    private RuleService ruleService;

    @InjectMocks
    private RuleController ruleController;

    private RuleDTO ruleDTO;

    @BeforeEach
    void setUp() {
        ruleDTO = RuleDTO.builder()
                .id("1")
                .name("Test Rule")
                .description("Test Description")
                .conditions("condition1")
                .actions("action1")
                .build();
    }

    @Test
    void createRule() {
        when(ruleService.createRule(any(RuleDTO.class))).thenReturn(ruleDTO);

        ResponseEntity<RuleDTO> response = ruleController.createRule(ruleDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(ruleDTO, response.getBody());
    }

    @Test
    void getRuleById() {
        when(ruleService.getRuleById(anyString())).thenReturn(ruleDTO);

        ResponseEntity<RuleDTO> response = ruleController.getRuleById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ruleDTO, response.getBody());
    }

    @Test
    void getAllRules() {
        List<RuleDTO> rules = Arrays.asList(ruleDTO);
        when(ruleService.getAllRules()).thenReturn(rules);

        ResponseEntity<List<RuleDTO>> response = ruleController.getAllRules();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateRule() {
        when(ruleService.updateRule(anyString(), any(RuleDTO.class))).thenReturn(ruleDTO);

        ResponseEntity<RuleDTO> response = ruleController.updateRule("1", ruleDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(ruleDTO, response.getBody());
    }

    @Test
    void deleteRule() {
        doNothing().when(ruleService).deleteRule(anyString());

        ResponseEntity<Void> response = ruleController.deleteRule("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
