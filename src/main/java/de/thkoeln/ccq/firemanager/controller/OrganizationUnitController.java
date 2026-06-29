package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.OrganizationUnitDTO;
import de.thkoeln.ccq.firemanager.service.OrganizationUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organization-units")
@RequiredArgsConstructor
public class OrganizationUnitController {

    private final OrganizationUnitService organizationUnitService;

    @PostMapping
    public ResponseEntity<OrganizationUnitDTO> createOrganizationUnit(@RequestBody OrganizationUnitDTO organizationUnitDTO) {
        OrganizationUnitDTO createdOrganizationUnit = organizationUnitService.createOrganizationUnit(organizationUnitDTO);
        return new ResponseEntity<>(createdOrganizationUnit, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationUnitDTO> getOrganizationUnitById(@PathVariable String id) {
        OrganizationUnitDTO organizationUnitDTO = organizationUnitService.getOrganizationUnitById(id);
        return ResponseEntity.ok(organizationUnitDTO);
    }

    @GetMapping
    public ResponseEntity<List<OrganizationUnitDTO>> getAllOrganizationUnits() {
        List<OrganizationUnitDTO> organizationUnits = organizationUnitService.getAllOrganizationUnits();
        return ResponseEntity.ok(organizationUnits);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrganizationUnitDTO> updateOrganizationUnit(@PathVariable String id, @RequestBody OrganizationUnitDTO organizationUnitDTO) {
        OrganizationUnitDTO updatedOrganizationUnit = organizationUnitService.updateOrganizationUnit(id, organizationUnitDTO);
        return ResponseEntity.ok(updatedOrganizationUnit);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizationUnit(@PathVariable String id) {
        organizationUnitService.deleteOrganizationUnit(id);
        return ResponseEntity.noContent().build();
    }
}
