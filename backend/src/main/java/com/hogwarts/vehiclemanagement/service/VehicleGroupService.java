package com.hogwarts.vehiclemanagement.service;

import com.hogwarts.vehiclemanagement.dto.VehicleGroupRequest;
import com.hogwarts.vehiclemanagement.dto.VehicleGroupResponse;
import com.hogwarts.vehiclemanagement.entity.VehicleGroup;
import com.hogwarts.vehiclemanagement.exception.VehicleGroupNotFoundException;
import com.hogwarts.vehiclemanagement.repository.VehicleGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleGroupService {

    private final VehicleGroupRepository vehicleGroupRepository;

    @Transactional
    public VehicleGroupResponse createVehicleGroup(VehicleGroupRequest request) {
        VehicleGroup vehicleGroup = VehicleGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        VehicleGroup savedVehicleGroup = vehicleGroupRepository.save(vehicleGroup);

        return mapToResponse(savedVehicleGroup);
    }

    @Transactional(readOnly = true)
    public List<VehicleGroupResponse> getAllVehicleGroups(Specification<VehicleGroup> spec, Pageable pageable) {
        Page<VehicleGroup> vehicleGroups = vehicleGroupRepository.findAll(spec, pageable);
        return vehicleGroups.getContent().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleGroupResponse getVehicleGroupById(Long id) {
        VehicleGroup vehicleGroup = vehicleGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new VehicleGroupNotFoundException("Fahrzeuggruppe mit ID " + id + " nicht gefunden"));
        return mapToResponse(vehicleGroup);
    }

    @Transactional
    public VehicleGroupResponse updateVehicleGroup(Long id, VehicleGroupRequest request) {
        VehicleGroup vehicleGroup = vehicleGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new VehicleGroupNotFoundException("Fahrzeuggruppe mit ID " + id + " nicht gefunden"));

        vehicleGroup.setName(request.getName());
        vehicleGroup.setDescription(request.getDescription());

        VehicleGroup updatedVehicleGroup = vehicleGroupRepository.save(vehicleGroup);

        return mapToResponse(updatedVehicleGroup);
    }

    @Transactional
    public void deleteVehicleGroup(Long id) {
        VehicleGroup vehicleGroup = vehicleGroupRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new VehicleGroupNotFoundException("Fahrzeuggruppe mit ID " + id + " nicht gefunden"));

        vehicleGroup.setDeleted(true);
        vehicleGroupRepository.save(vehicleGroup);
    }

    private VehicleGroupResponse mapToResponse(VehicleGroup vehicleGroup) {
        return VehicleGroupResponse.builder()
                .id(vehicleGroup.getId())
                .name(vehicleGroup.getName())
                .description(vehicleGroup.getDescription())
                .createdAt(vehicleGroup.getCreatedAt())
                .updatedAt(vehicleGroup.getUpdatedAt())
                .deleted(vehicleGroup.isDeleted())
                .build();
    }
}
