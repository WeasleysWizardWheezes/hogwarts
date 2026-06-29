package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.RuleDTO;
import de.thkoeln.ccq.firemanager.service.RuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@RequiredArgsConstructor
public class RuleController {

    private final RuleService ruleService;

    @PostMapping
    public ResponseEntity<RuleDTO> createRule(@RequestBody RuleDTO ruleDTO) {
        RuleDTO createdRule = ruleService.createRule(ruleDTO);
        return new ResponseEntity<>(createdRule, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RuleDTO> getRuleById(@PathVariable String id) {
        RuleDTO ruleDTO = ruleService.getRuleById(id);
        return ResponseEntity.ok(ruleDTO);
    }

    @GetMapping
    public ResponseEntity<List<RuleDTO>> getAllRules() {
        List<RuleDTO> rules = ruleService.getAllRules();
        return ResponseEntity.ok(rules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RuleDTO> updateRule(@PathVariable String id, @RequestBody RuleDTO ruleDTO) {
        RuleDTO updatedRule = ruleService.updateRule(id, ruleDTO);
        return ResponseEntity.ok(updatedRule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable String id) {
        ruleService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
