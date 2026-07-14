package de.thkoeln.ccq.firemanager.equipment.api;

import de.thkoeln.ccq.firemanager.equipment.application.EquipmentService;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentId;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material-geraete")
public class EquipmentController {
    private final EquipmentService equipmentService;
    private final EquipmentApiMapper mapper;

    public EquipmentController(EquipmentService equipmentService, EquipmentApiMapper mapper) {
        this.equipmentService = equipmentService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<List<EquipmentResponse>> getAllEquipment() {
        var equipmentList = equipmentService.getAllEquipment();
        var responseList = equipmentList.stream()
            .map(mapper::toResponse)
            .toList();
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipmentResponse> getEquipmentById(@PathVariable String id) {
        return equipmentService.getEquipment(EquipmentId.from(java.util.UUID.fromString(id)))
            .map(mapper::toResponse)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EquipmentResponse> createEquipment(@RequestBody CreateEquipmentRequest request) {
        var equipment = equipmentService.createEquipment(
            request.name(),
            request.serialNumber(),
            request.type(),
            request.storageLocation(),
            request.description(),
            request.acquisitionDate(),
            request.manufacturer()
        );
        var response = mapper.toResponse(equipment);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<List<EquipmentResponse>> searchEquipment(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String serialNumber,
        @RequestParam(required = false) String storageLocation
    ) {
        List<EquipmentResponse> results;
        
        if (name != null) {
            results = equipmentService.searchEquipmentByName(name).stream()
                .map(mapper::toResponse)
                .toList();
        } else if (serialNumber != null) {
            results = equipmentService.searchEquipmentBySerialNumber(serialNumber).stream()
                .map(mapper::toResponse)
                .toList();
        } else if (storageLocation != null) {
            results = equipmentService.searchEquipmentByStorageLocation(storageLocation).stream()
                .map(mapper::toResponse)
                .toList();
        } else {
            results = equipmentService.getAllEquipment().stream()
                .map(mapper::toResponse)
                .toList();
        }
        
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable String id) {
        equipmentService.deleteEquipment(EquipmentId.from(java.util.UUID.fromString(id)));
        return ResponseEntity.noContent().build();
    }
}
