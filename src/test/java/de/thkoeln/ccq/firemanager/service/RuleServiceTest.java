package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.RuleDTO;
import de.thkoeln.ccq.firemanager.model.Rule;
import de.thkoeln.ccq.firemanager.repository.RuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RuleServiceTest {

    @Mock
    private RuleRepository ruleRepository;

    @InjectMocks
    private RuleService ruleService;

    private RuleDTO ruleDTO;
    private Rule rule;

    @BeforeEach
    void setUp() {
        ruleDTO = RuleDTO.builder()
                .id("1")
                .name("Test Rule")
                .description("Test Description")
                .conditions("condition1")
                .actions("action1")
                .build();

        rule = Rule.builder()
                .id("1")
                .name("Test Rule")
                .description("Test Description")
                .conditions("condition1")
                .actions("action1")
                .build();
    }

    @Test
    void createRule() {
        when(ruleRepository.save(any(Rule.class))).thenReturn(rule);

        RuleDTO result = ruleService.createRule(ruleDTO);

        assertNotNull(result);
        assertEquals(ruleDTO.getId(), result.getId());
        assertEquals(ruleDTO.getName(), result.getName());
        assertEquals(ruleDTO.getConditions(), result.getConditions());
    }

    @Test
    void getRuleById() {
        when(ruleRepository.findById("1")).thenReturn(Optional.of(rule));

        RuleDTO result = ruleService.getRuleById("1");

        assertNotNull(result);
        assertEquals(rule.getId(), result.getId());
        assertEquals(rule.getName(), result.getName());
    }

    @Test
    void getRuleById_NotFound() {
        when(ruleRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            ruleService.getRuleById("1");
        });
    }

    @Test
    void getAllRules() {
        List<Rule> rules = Arrays.asList(rule);
        when(ruleRepository.findAll()).thenReturn(rules);

        List<RuleDTO> result = ruleService.getAllRules();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(rule.getId(), result.get(0).getId());
    }

    @Test
    void updateRule() {
        when(ruleRepository.findById("1")).thenReturn(Optional.of(rule));
        when(ruleRepository.save(any(Rule.class))).thenReturn(rule);

        RuleDTO result = ruleService.updateRule("1", ruleDTO);

        assertNotNull(result);
        assertEquals(ruleDTO.getId(), result.getId());
        assertEquals(ruleDTO.getName(), result.getName());
    }

    @Test
    void updateRule_NotFound() {
        when(ruleRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            ruleService.updateRule("1", ruleDTO);
        });
    }

    @Test
    void deleteRule() {
        doNothing().when(ruleRepository).deleteById("1");

        assertDoesNotThrow(() -> {
            ruleService.deleteRule("1");
        });

        verify(ruleRepository, times(1)).deleteById("1");
    }
}
