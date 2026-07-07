package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.AssignEquipmentRequest;
import de.thkoeln.ccq.firemanager.dto.CreateOperationRequest;
import de.thkoeln.ccq.firemanager.dto.EquipmentSummary;
import de.thkoeln.ccq.firemanager.dto.OperationResponse;
import de.thkoeln.ccq.firemanager.service.OperationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/operations")
public class OperationController {
    
    @Autowired
    private OperationService operationService;
    
    @PostMapping
    public ResponseEntity<OperationResponse> createOperation(@Valid @RequestBody CreateOperationRequest request) {
        OperationResponse response = operationService.createOperation(request);
        URI location = URI.create("/api/v1/operations/" + response.id());
        return ResponseEntity.created(location).body(response);
    }
    
    @GetMapping
    public ResponseEntity<List<OperationResponse>> getAllOperations() {
        List<OperationResponse> operations = operationService.getAllOperations();
        return ResponseEntity.ok(operations);
    }
    
    @GetMapping("/{operationId}")
    public ResponseEntity<OperationResponse> getOperationById(@PathVariable UUID operationId) {
        OperationResponse response = operationService.getOperationById(operationId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{operationId}/assign")
    public ResponseEntity<OperationResponse> assignEquipment(
            @PathVariable UUID operationId,
            @Valid @RequestBody AssignEquipmentRequest request) {
        OperationResponse response = operationService.assignEquipment(operationId, request);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/available-equipment")
    public ResponseEntity<List<EquipmentSummary>> getAvailableEquipment(
            @RequestParam String operationType) {
        List<EquipmentSummary> equipment = operationService.getAvailableEquipment(operationType);
        return ResponseEntity.ok(equipment);
    }
}