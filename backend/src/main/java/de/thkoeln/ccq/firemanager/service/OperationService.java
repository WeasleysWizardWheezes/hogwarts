package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.AssignEquipmentRequest;
import de.thkoeln.ccq.firemanager.dto.CreateOperationRequest;
import de.thkoeln.ccq.firemanager.dto.EquipmentSummary;
import de.thkoeln.ccq.firemanager.dto.OperationResponse;
import de.thkoeln.ccq.firemanager.model.Operation;
import de.thkoeln.ccq.firemanager.repository.OperationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OperationService {
    
    @Autowired
    private OperationRepository operationRepository;
    
    public OperationResponse createOperation(CreateOperationRequest request) {
        Operation operation = new Operation(
            request.title(),
            request.operationType(),
            request.startTime(),
            request.endTime(),
            request.requiredEquipment(),
            request.description()
        );
        
        Operation savedOperation = operationRepository.save(operation);
        return mapToResponse(savedOperation);
    }
    
    public OperationResponse getOperationById(UUID id) {
        Operation operation = operationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Operation not found with id: " + id));
        return mapToResponse(operation);
    }
    
    public List<OperationResponse> getAllOperations() {
        return operationRepository.findAll().stream()
            .map(this::mapToResponse)
            .toList();
    }
    
    public OperationResponse assignEquipment(UUID operationId, AssignEquipmentRequest request) {
        Operation operation = operationRepository.findById(operationId)
            .orElseThrow(() -> new RuntimeException("Operation not found with id: " + operationId));
        
        // In a real implementation, this would assign equipment to the operation
        // For now, we just return the updated operation
        return mapToResponse(operation);
    }
    
    public List<EquipmentSummary> getAvailableEquipment(String operationType) {
        // In a real implementation, this would return available equipment based on operation type
        // For now, we return an empty list
        return List.of();
    }
    
    private OperationResponse mapToResponse(Operation operation) {
        return new OperationResponse(
            operation.getId(),
            operation.getTitle(),
            operation.getOperationType(),
            operation.getStartTime(),
            operation.getEndTime(),
            operation.getRequiredEquipment(),
            operation.getDescription(),
            operation.getCreatedAt(),
            operation.getUpdatedAt()
        );
    }
}