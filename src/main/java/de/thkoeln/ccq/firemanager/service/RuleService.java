package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.RuleDTO;
import de.thkoeln.ccq.firemanager.model.Rule;
import de.thkoeln.ccq.firemanager.repository.RuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RuleService {

    private final RuleRepository ruleRepository;

    public RuleDTO createRule(RuleDTO ruleDTO) {
        Rule rule = mapToEntity(ruleDTO);
        Rule savedRule = ruleRepository.save(rule);
        return mapToDTO(savedRule);
    }

    public RuleDTO getRuleById(String id) {
        Rule rule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        return mapToDTO(rule);
    }

    public List<RuleDTO> getAllRules() {
        return ruleRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public RuleDTO updateRule(String id, RuleDTO ruleDTO) {
        Rule existingRule = ruleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rule not found"));
        
        existingRule.setName(ruleDTO.getName());
        existingRule.setDescription(ruleDTO.getDescription());
        existingRule.setConditions(ruleDTO.getConditions());
        existingRule.setActions(ruleDTO.getActions());
        
        Rule updatedRule = ruleRepository.save(existingRule);
        return mapToDTO(updatedRule);
    }

    public void deleteRule(String id) {
        ruleRepository.deleteById(id);
    }

    private Rule mapToEntity(RuleDTO ruleDTO) {
        return Rule.builder()
                .id(ruleDTO.getId())
                .name(ruleDTO.getName())
                .description(ruleDTO.getDescription())
                .conditions(ruleDTO.getConditions())
                .actions(ruleDTO.getActions())
                .build();
    }

    private RuleDTO mapToDTO(Rule rule) {
        return RuleDTO.builder()
                .id(rule.getId())
                .name(rule.getName())
                .description(rule.getDescription())
                .conditions(rule.getConditions())
                .actions(rule.getActions())
                .build();
    }
}
