package com.hogwarts.vehiclemanagement.service;

import com.hogwarts.vehiclemanagement.dto.VehicleRequest;
import com.hogwarts.vehiclemanagement.dto.VehicleResponse;
import com.hogwarts.vehiclemanagement.entity.Vehicle;
import com.hogwarts.vehiclemanagement.entity.VehicleGroup;
import com.hogwarts.vehiclemanagement.exception.VehicleGroupNotFoundException;
import com.hogwarts.vehiclemanagement.exception.VehicleNotFoundException;
import com.hogwarts.vehiclemanagement.repository.VehicleGroupRepository;
import com.hogwarts.vehiclemanagement.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final VehicleGroupRepository vehicleGroupRepository;

    @Transactional
    public VehicleResponse createVehicle(VehicleRequest request) {
        VehicleGroup vehicleGroup = vehicleGroupRepository.findByIdAndDeletedFalse(request.getVehicleGroupId())
                .orElseThrow(() -> new VehicleGroupNotFoundException("Fahrzeuggruppe mit ID " + request.getVehicleGroupId() + " nicht gefunden"));

        Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(request.getStatus());

        Vehicle vehicle = Vehicle.builder()
                .name(request.getName())
                .radioCallName(request.getRadioCallName())
                .licensePlate(request.getLicensePlate())
                .year(request.getYear())
                .description(request.getDescription())
                .vehicleGroup(vehicleGroup)
                .status(status)
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return mapToResponse(savedVehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles(Specification<Vehicle> spec, Pageable pageable) {
        Page<Vehicle> vehicles = vehicleRepository.findAll(spec, pageable);
        return vehicles.getContent().stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public VehicleResponse getVehicleById(Long id) {
        Vehicle vehicle = vehicleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new VehicleNotFoundException("Fahrzeug mit ID " + id + " nicht gefunden"));
        return mapToResponse(vehicle);
    }

    @Transactional
    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        Vehicle vehicle = vehicleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new VehicleNotFoundException("Fahrzeug mit ID " + id + " nicht gefunden"));

        VehicleGroup vehicleGroup = vehicleGroupRepository.findByIdAndDeletedFalse(request.getVehicleGroupId())
                .orElseThrow(() -> new VehicleGroupNotFoundException("Fahrzeuggruppe mit ID " + request.getVehicleGroupId() + " nicht gefunden"));

        Vehicle.VehicleStatus status = Vehicle.VehicleStatus.valueOf(request.getStatus());

        vehicle.setName(request.getName());
        vehicle.setRadioCallName(request.getRadioCallName());
        vehicle.setLicensePlate(request.getLicensePlate());
        vehicle.setYear(request.getYear());
        vehicle.setDescription(request.getDescription());
        vehicle.setVehicleGroup(vehicleGroup);
        vehicle.setStatus(status);

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);

        return mapToResponse(updatedVehicle);
    }

    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new VehicleNotFoundException("Fahrzeug mit ID " + id + " nicht gefunden"));

        vehicle.setDeleted(true);
        vehicleRepository.save(vehicle);
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .name(vehicle.getName())
                .radioCallName(vehicle.getRadioCallName())
                .licensePlate(vehicle.getLicensePlate())
                .year(vehicle.getYear())
                .description(vehicle.getDescription())
                .vehicleGroupId(vehicle.getVehicleGroup().getId())
                .status(vehicle.getStatus().name())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .deleted(vehicle.isDeleted())
                .build();
    }
}
