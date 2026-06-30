package de.thkoeln.ccq.firemanager.equipment.controller;

import de.thkoeln.ccq.firemanager.equipment.service.EquipmentService;
import de.thkoeln.ccq.firemanager.equipment.web.EquipmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<EquipmentDto> createEquipment(@RequestBody EquipmentDto equipmentDto) {
        EquipmentDto createdEquipment = equipmentService.createEquipment(equipmentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEquipment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentDto> getEquipmentById(@PathVariable UUID id) {
        EquipmentDto equipmentDto = equipmentService.getEquipmentById(id);
        return ResponseEntity.ok(equipmentDto);
    }
}