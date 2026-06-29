package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.OrganizationUnitDTO;
import de.thkoeln.ccq.firemanager.service.OrganizationUnitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationUnitControllerTest {

    @Mock
    private OrganizationUnitService organizationUnitService;

    @InjectMocks
    private OrganizationUnitController organizationUnitController;

    private OrganizationUnitDTO organizationUnitDTO;

    @BeforeEach
    void setUp() {
        organizationUnitDTO = OrganizationUnitDTO.builder()
                .id("1")
                .name("Test Unit")
                .description("Test Description")
                .build();
    }

    @Test
    void createOrganizationUnit() {
        when(organizationUnitService.createOrganizationUnit(any(OrganizationUnitDTO.class))).thenReturn(organizationUnitDTO);

        ResponseEntity<OrganizationUnitDTO> response = organizationUnitController.createOrganizationUnit(organizationUnitDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(organizationUnitDTO, response.getBody());
    }

    @Test
    void getOrganizationUnitById() {
        when(organizationUnitService.getOrganizationUnitById(anyString())).thenReturn(organizationUnitDTO);

        ResponseEntity<OrganizationUnitDTO> response = organizationUnitController.getOrganizationUnitById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(organizationUnitDTO, response.getBody());
    }

    @Test
    void getAllOrganizationUnits() {
        List<OrganizationUnitDTO> organizationUnits = Arrays.asList(organizationUnitDTO);
        when(organizationUnitService.getAllOrganizationUnits()).thenReturn(organizationUnits);

        ResponseEntity<List<OrganizationUnitDTO>> response = organizationUnitController.getAllOrganizationUnits();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateOrganizationUnit() {
        when(organizationUnitService.updateOrganizationUnit(anyString(), any(OrganizationUnitDTO.class))).thenReturn(organizationUnitDTO);

        ResponseEntity<OrganizationUnitDTO> response = organizationUnitController.updateOrganizationUnit("1", organizationUnitDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(organizationUnitDTO, response.getBody());
    }

    @Test
    void deleteOrganizationUnit() {
        doNothing().when(organizationUnitService).deleteOrganizationUnit(anyString());

        ResponseEntity<Void> response = organizationUnitController.deleteOrganizationUnit("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
