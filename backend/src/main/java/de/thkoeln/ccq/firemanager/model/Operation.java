package de.thkoeln.ccq.firemanager.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "operations")
public class Operation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String title;
    
    private String operationType;
    
    private LocalDateTime startTime;
    
    private LocalDateTime endTime;
    
    @ElementCollection
    @CollectionTable(name = "operation_required_equipment")
    @Column(name = "equipment_id")
    private List<UUID> requiredEquipment;
    
    private String description;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    // Constructors
    public Operation() {}
    
    public Operation(String title, String operationType, LocalDateTime startTime, 
                   LocalDateTime endTime, List<UUID> requiredEquipment, String description) {
        this.title = title;
        this.operationType = operationType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.requiredEquipment = requiredEquipment;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public List<UUID> getRequiredEquipment() {
        return requiredEquipment;
    }
    
    public void setRequiredEquipment(List<UUID> requiredEquipment) {
        this.requiredEquipment = requiredEquipment;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}