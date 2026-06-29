package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.OrganizationUnitDTO;
import de.thkoeln.ccq.firemanager.model.OrganizationUnit;
import de.thkoeln.ccq.firemanager.repository.OrganizationUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrganizationUnitServiceTest {

    @Mock
    private OrganizationUnitRepository organizationUnitRepository;

    @InjectMocks
    private OrganizationUnitService organizationUnitService;

    private OrganizationUnitDTO organizationUnitDTO;
    private OrganizationUnit organizationUnit;

    @BeforeEach
    void setUp() {
        organizationUnitDTO = OrganizationUnitDTO.builder()
                .id("1")
                .name("Test Unit")
                .description("Test Description")
                .build();

        organizationUnit = OrganizationUnit.builder()
                .id("1")
                .name("Test Unit")
                .description("Test Description")
                .build();
    }

    @Test
    void createOrganizationUnit() {
        when(organizationUnitRepository.save(any(OrganizationUnit.class))).thenReturn(organizationUnit);

        OrganizationUnitDTO result = organizationUnitService.createOrganizationUnit(organizationUnitDTO);

        assertNotNull(result);
        assertEquals(organizationUnitDTO.getId(), result.getId());
        assertEquals(organizationUnitDTO.getName(), result.getName());
        assertEquals(organizationUnitDTO.getDescription(), result.getDescription());
    }

    @Test
    void getOrganizationUnitById() {
        when(organizationUnitRepository.findById("1")).thenReturn(Optional.of(organizationUnit));

        OrganizationUnitDTO result = organizationUnitService.getOrganizationUnitById("1");

        assertNotNull(result);
        assertEquals(organizationUnit.getId(), result.getId());
        assertEquals(organizationUnit.getName(), result.getName());
    }

    @Test
    void getOrganizationUnitById_NotFound() {
        when(organizationUnitRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            organizationUnitService.getOrganizationUnitById("1");
        });
    }

    @Test
    void getAllOrganizationUnits() {
        List<OrganizationUnit> organizationUnits = Arrays.asList(organizationUnit);
        when(organizationUnitRepository.findAll()).thenReturn(organizationUnits);

        List<OrganizationUnitDTO> result = organizationUnitService.getAllOrganizationUnits();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(organizationUnit.getId(), result.get(0).getId());
    }

    @Test
    void updateOrganizationUnit() {
        when(organizationUnitRepository.findById("1")).thenReturn(Optional.of(organizationUnit));
        when(organizationUnitRepository.save(any(OrganizationUnit.class))).thenReturn(organizationUnit);

        OrganizationUnitDTO result = organizationUnitService.updateOrganizationUnit("1", organizationUnitDTO);

        assertNotNull(result);
        assertEquals(organizationUnitDTO.getId(), result.getId());
        assertEquals(organizationUnitDTO.getName(), result.getName());
    }

    @Test
    void updateOrganizationUnit_NotFound() {
        when(organizationUnitRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            organizationUnitService.updateOrganizationUnit("1", organizationUnitDTO);
        });
    }

    @Test
    void deleteOrganizationUnit() {
        doNothing().when(organizationUnitRepository).deleteById("1");

        assertDoesNotThrow(() -> {
            organizationUnitService.deleteOrganizationUnit("1");
        });

        verify(organizationUnitRepository, times(1)).deleteById("1");
    }
}
