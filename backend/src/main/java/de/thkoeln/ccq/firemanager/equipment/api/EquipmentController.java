package de.thkoeln.ccq.firemanager.equipment.api;

import de.thkoeln.ccq.firemanager.equipment.application.EquipmentApplicationService;
import de.thkoeln.ccq.firemanager.equipment.application.EquipmentAlreadyExistsException;
import de.thkoeln.ccq.firemanager.equipment.application.EquipmentNotFoundException;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipment")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentApplicationService equipmentService;
    private final EquipmentApiMapper mapper;

    @GetMapping
    public ResponseEntity<EquipmentPageResponse> getAllEquipment(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String sort
    ) {
        Pageable pageable = PageRequest.of(page, size);
        
        List<EquipmentResponse> equipmentList = equipmentService.searchEquipment(q, location).stream()
                .map(mapper::toResponse)
                .toList();
        
        // Simple pagination for now - in a real scenario, we'd use proper pagination
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), equipmentList.size());
        List<EquipmentResponse> pageContent = equipmentList.subList(start, end);
        
        var response = new EquipmentPageResponse(
                pageContent,
                page,
                size,
                equipmentList.size(),
                (int) Math.ceil((double) equipmentList.size() / size)
        );
        
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<EquipmentResponse> createEquipment(
            @Valid @RequestBody CreateEquipmentRequest request
    ) {
        var equipment = equipmentService.createEquipment(
                request.name(),
                request.serialNumber(),
                request.type(),
                request.location(),
                request.description()
        );
        
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(equipment.id().value())
                .toUri();
        
        return ResponseEntity.created(location).body(mapper.toResponse(equipment));
    }

    @GetMapping("/{equipmentId}")
    public ResponseEntity<EquipmentResponse> getEquipment(@PathVariable UUID equipmentId) {
        var equipment = equipmentService.getEquipment(EquipmentId.from(equipmentId))
            .orElseThrow(() -> new EquipmentNotFoundException(EquipmentId.from(equipmentId)));
        
        return ResponseEntity.ok(mapper.toResponse(equipment));
    }

    @PutMapping("/{equipmentId}")
    public ResponseEntity<EquipmentResponse> updateEquipment(
            @PathVariable UUID equipmentId,
            @Valid @RequestBody UpdateEquipmentRequest request
    ) {
        var equipment = equipmentService.updateEquipment(
                EquipmentId.from(equipmentId),
                request.name(),
                request.serialNumber(),
                request.type(),
                request.location(),
                request.description()
        );
        
        return ResponseEntity.ok(mapper.toResponse(equipment));
    }

    @DeleteMapping("/{equipmentId}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable UUID equipmentId) {
        equipmentService.deleteEquipment(EquipmentId.from(equipmentId));
        return ResponseEntity.noContent().build();
    }
}