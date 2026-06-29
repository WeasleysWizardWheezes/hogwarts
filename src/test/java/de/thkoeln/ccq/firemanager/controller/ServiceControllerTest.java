package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.ServiceDTO;
import de.thkoeln.ccq.firemanager.service.ServiceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceControllerTest {

    @Mock
    private ServiceService serviceService;

    @InjectMocks
    private ServiceController serviceController;

    private ServiceDTO serviceDTO;

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
    }

    @Test
    void createService() {
        when(serviceService.createService(any(ServiceDTO.class))).thenReturn(serviceDTO);

        ResponseEntity<ServiceDTO> response = serviceController.createService(serviceDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(serviceDTO, response.getBody());
    }

    @Test
    void getServiceById() {
        when(serviceService.getServiceById(anyString())).thenReturn(serviceDTO);

        ResponseEntity<ServiceDTO> response = serviceController.getServiceById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceDTO, response.getBody());
    }

    @Test
    void getAllServices() {
        List<ServiceDTO> services = Arrays.asList(serviceDTO);
        when(serviceService.getAllServices()).thenReturn(services);

        ResponseEntity<List<ServiceDTO>> response = serviceController.getAllServices();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getServicesByOrganizationUnit() {
        List<ServiceDTO> services = Arrays.asList(serviceDTO);
        when(serviceService.getServicesByOrganizationUnit(anyString())).thenReturn(services);

        ResponseEntity<List<ServiceDTO>> response = serviceController.getServicesByOrganizationUnit("org1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateService() {
        when(serviceService.updateService(anyString(), any(ServiceDTO.class))).thenReturn(serviceDTO);

        ResponseEntity<ServiceDTO> response = serviceController.updateService("1", serviceDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(serviceDTO, response.getBody());
    }

    @Test
    void deleteService() {
        doNothing().when(serviceService).deleteService(anyString());

        ResponseEntity<Void> response = serviceController.deleteService("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
