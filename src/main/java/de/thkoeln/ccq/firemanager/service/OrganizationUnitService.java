package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.OrganizationUnitDTO;
import de.thkoeln.ccq.firemanager.model.OrganizationUnit;
import de.thkoeln.ccq.firemanager.repository.OrganizationUnitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrganizationUnitService {

    private final OrganizationUnitRepository organizationUnitRepository;

    public OrganizationUnitDTO createOrganizationUnit(OrganizationUnitDTO organizationUnitDTO) {
        OrganizationUnit organizationUnit = mapToEntity(organizationUnitDTO);
        OrganizationUnit savedOrganizationUnit = organizationUnitRepository.save(organizationUnit);
        return mapToDTO(savedOrganizationUnit);
    }

    public OrganizationUnitDTO getOrganizationUnitById(String id) {
        OrganizationUnit organizationUnit = organizationUnitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrganizationUnit not found"));
        return mapToDTO(organizationUnit);
    }

    public List<OrganizationUnitDTO> getAllOrganizationUnits() {
        return organizationUnitRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public OrganizationUnitDTO updateOrganizationUnit(String id, OrganizationUnitDTO organizationUnitDTO) {
        OrganizationUnit existingOrganizationUnit = organizationUnitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrganizationUnit not found"));
        
        existingOrganizationUnit.setName(organizationUnitDTO.getName());
        existingOrganizationUnit.setDescription(organizationUnitDTO.getDescription());
        
        OrganizationUnit updatedOrganizationUnit = organizationUnitRepository.save(existingOrganizationUnit);
        return mapToDTO(updatedOrganizationUnit);
    }

    public void deleteOrganizationUnit(String id) {
        organizationUnitRepository.deleteById(id);
    }

    private OrganizationUnit mapToEntity(OrganizationUnitDTO organizationUnitDTO) {
        return OrganizationUnit.builder()
                .id(organizationUnitDTO.getId())
                .name(organizationUnitDTO.getName())
                .description(organizationUnitDTO.getDescription())
                .build();
    }

    private OrganizationUnitDTO mapToDTO(OrganizationUnit organizationUnit) {
        return OrganizationUnitDTO.builder()
                .id(organizationUnit.getId())
                .name(organizationUnit.getName())
                .description(organizationUnit.getDescription())
                .build();
    }
}
