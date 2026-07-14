package de.thkoeln.ccq.firemanager.inventory.controller;

import de.thkoeln.ccq.firemanager.inventory.dto.EquipmentCreateDTO;
import de.thkoeln.ccq.firemanager.inventory.dto.EquipmentDTO;
import de.thkoeln.ccq.firemanager.inventory.dto.EquipmentUpdateDTO;
import de.thkoeln.ccq.firemanager.inventory.service.EquipmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory/equipment")
@Tag(name = "Equipment", description = "Equipment management APIs")
@RequiredArgsConstructor
public class EquipmentController {
    
    private final EquipmentService equipmentService;
    
    @GetMapping
    @Operation(summary = "Get all equipment", description = "Returns a list of all equipment items")
    public ResponseEntity<List<EquipmentDTO>> getAllEquipment(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String location) {
        List<EquipmentDTO> equipmentList = equipmentService.getAllEquipment(search, location);
        return ResponseEntity.ok(equipmentList);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get equipment by ID", description = "Returns a single equipment item by ID")
    public ResponseEntity<EquipmentDTO> getEquipmentById(@PathVariable Long id) {
        EquipmentDTO equipment = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(equipment);
    }
    
    @PostMapping
    @Operation(summary = "Create new equipment", description = "Creates a new equipment item")
    public ResponseEntity<EquipmentDTO> createEquipment(@Valid @RequestBody EquipmentCreateDTO equipmentCreateDTO) {
        EquipmentDTO createdEquipment = equipmentService.createEquipment(equipmentCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEquipment);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update equipment", description = "Updates an existing equipment item")
    public ResponseEntity<EquipmentDTO> updateEquipment(
            @PathVariable Long id, 
            @Valid @RequestBody EquipmentUpdateDTO equipmentUpdateDTO) {
        EquipmentDTO updatedEquipment = equipmentService.updateEquipment(id, equipmentUpdateDTO);
        return ResponseEntity.ok(updatedEquipment);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete equipment", description = "Deletes an equipment item")
    public ResponseEntity<Void> deleteEquipment(@PathVariable Long id) {
        equipmentService.deleteEquipment(id);
        return ResponseEntity.noContent().build();
    }
}