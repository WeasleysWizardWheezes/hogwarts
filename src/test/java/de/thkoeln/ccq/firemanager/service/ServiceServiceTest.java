package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.ServiceDTO;
import de.thkoeln.ccq.firemanager.model.Service;
import de.thkoeln.ccq.firemanager.repository.ServiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceServiceTest {

    @Mock
    private ServiceRepository serviceRepository;

    @InjectMocks
    private ServiceService serviceService;

    private ServiceDTO serviceDTO;
    private Service service;

    @BeforeEach
    void setUp() {
        serviceDTO = ServiceDTO.builder()
                .id("1")
                .title("Test Service")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .organizationUnitId("org1")
                .build();

        service = Service.builder()
                .id("1")
                .title("Test Service")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();
    }

    @Test
    void createService() {
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        ServiceDTO result = serviceService.createService(serviceDTO);

        assertNotNull(result);
        assertEquals(serviceDTO.getId(), result.getId());
        assertEquals(serviceDTO.getTitle(), result.getTitle());
        assertEquals(serviceDTO.getStartDate(), result.getStartDate());
    }

    @Test
    void getServiceById() {
        when(serviceRepository.findById("1")).thenReturn(Optional.of(service));

        ServiceDTO result = serviceService.getServiceById("1");

        assertNotNull(result);
        assertEquals(service.getId(), result.getId());
        assertEquals(service.getTitle(), result.getTitle());
    }

    @Test
    void getServiceById_NotFound() {
        when(serviceRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            serviceService.getServiceById("1");
        });
    }

    @Test
    void getAllServices() {
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findAll()).thenReturn(services);

        List<ServiceDTO> result = serviceService.getAllServices();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(service.getId(), result.get(0).getId());
    }

    @Test
    void getServicesByOrganizationUnit() {
        List<Service> services = Arrays.asList(service);
        when(serviceRepository.findByOrganizationUnitId("org1")).thenReturn(services);

        List<ServiceDTO> result = serviceService.getServicesByOrganizationUnit("org1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(service.getId(), result.get(0).getId());
    }

    @Test
    void updateService() {
        when(serviceRepository.findById("1")).thenReturn(Optional.of(service));
        when(serviceRepository.save(any(Service.class))).thenReturn(service);

        ServiceDTO result = serviceService.updateService("1", serviceDTO);

        assertNotNull(result);
        assertEquals(serviceDTO.getId(), result.getId());
        assertEquals(serviceDTO.getTitle(), result.getTitle());
    }

    @Test
    void updateService_NotFound() {
        when(serviceRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            serviceService.updateService("1", serviceDTO);
        });
    }

    @Test
    void deleteService() {
        doNothing().when(serviceRepository).deleteById("1");

        assertDoesNotThrow(() -> {
            serviceService.deleteService("1");
        });

        verify(serviceRepository, times(1)).deleteById("1");
    }
}
