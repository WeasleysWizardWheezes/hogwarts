package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.ServiceDTO;
import de.thkoeln.ccq.firemanager.model.Service;
import de.thkoeln.ccq.firemanager.repository.ServiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceService {

    private final ServiceRepository serviceRepository;

    public ServiceDTO createService(ServiceDTO serviceDTO) {
        Service service = mapToEntity(serviceDTO);
        Service savedService = serviceRepository.save(service);
        return mapToDTO(savedService);
    }

    public ServiceDTO getServiceById(String id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        return mapToDTO(service);
    }

    public List<ServiceDTO> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ServiceDTO> getServicesByOrganizationUnit(String organizationUnitId) {
        return serviceRepository.findByOrganizationUnitId(organizationUnitId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ServiceDTO updateService(String id, ServiceDTO serviceDTO) {
        Service existingService = serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
        
        existingService.setTitle(serviceDTO.getTitle());
        existingService.setDescription(serviceDTO.getDescription());
        existingService.setStartDate(serviceDTO.getStartDate());
        existingService.setEndDate(serviceDTO.getEndDate());
        existingService.setRecurring(serviceDTO.isRecurring());
        existingService.setRecurrencePattern(serviceDTO.getRecurrencePattern());
        
        Service updatedService = serviceRepository.save(existingService);
        return mapToDTO(updatedService);
    }

    public void deleteService(String id) {
        serviceRepository.deleteById(id);
    }

    private Service mapToEntity(ServiceDTO serviceDTO) {
        return Service.builder()
                .id(serviceDTO.getId())
                .title(serviceDTO.getTitle())
                .description(serviceDTO.getDescription())
                .startDate(serviceDTO.getStartDate())
                .endDate(serviceDTO.getEndDate())
                .isRecurring(serviceDTO.isRecurring())
                .recurrencePattern(serviceDTO.getRecurrencePattern())
                .build();
    }

    private ServiceDTO mapToDTO(Service service) {
        return ServiceDTO.builder()
                .id(service.getId())
                .title(service.getTitle())
                .description(service.getDescription())
                .startDate(service.getStartDate())
                .endDate(service.getEndDate())
                .isRecurring(service.isRecurring())
                .recurrencePattern(service.getRecurrencePattern())
                .organizationUnitId(service.getOrganizationUnit() != null ? service.getOrganizationUnit().getId() : null)
                .build();
    }
}
