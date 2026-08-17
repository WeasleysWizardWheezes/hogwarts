package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.vehicle.VehicleService;
import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupConflictException;
import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(VehicleGroupController.class)
class VehicleGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleGroupService vehicleGroupServiceStub;

    @MockitoBean
    private VehicleService vehicleServiceStub;

    @Test
    void createVehicleGroup_returnsCreated() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Alle Löschfahrzeuge");
        when(vehicleGroupServiceStub.create("Löschfahrzeuge", "Alle Löschfahrzeuge"))
                .thenReturn(vehicleGroup);

        // Act & Assert
        mockMvc.perform(post("/api/v1/vehicle-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Löschfahrzeuge", "beschreibung": "Alle Löschfahrzeuge"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(vehicleGroup.getId().toString()))
                .andExpect(jsonPath("$.name").value("Löschfahrzeuge"))
                .andExpect(jsonPath("$.beschreibung").value("Alle Löschfahrzeuge"));
    }

    @Test
    void createVehicleGroup_returns400WhenNameIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/vehicle-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"beschreibung": "Beschreibung"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getVehicleGroup_returnsOk() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupServiceStub.getById(vehicleGroup.getId())).thenReturn(vehicleGroup);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicle-groups/{id}", vehicleGroup.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicleGroup.getId().toString()))
                .andExpect(jsonPath("$.name").value("Löschfahrzeuge"));
    }

    @Test
    void getVehicleGroup_returns404WhenNotFound() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupServiceStub.getById(vehicleGroupId))
                .thenThrow(new VehicleGroupNotFoundException(vehicleGroupId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicle-groups/{id}", vehicleGroupId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(vehicleGroupId.toString())));
    }

    @Test
    void listVehicleGroups_returnsOk() throws Exception {
        // Arrange
        var group1 = new VehicleGroup("Löschfahrzeuge", "Beschreibung 1");
        var group2 = new VehicleGroup("Rettungsdienst", "Beschreibung 2");
        var page = new PageImpl<>(List.of(group1, group2), PageRequest.of(0, 20), 2);
        when(vehicleGroupServiceStub.getAll(0, 20)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicle-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.page.totalElements").value(2));
    }

    @Test
    void updateVehicleGroup_returnsOk() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge (aktualisiert)", "Neue Beschreibung");
        when(vehicleGroupServiceStub.update(vehicleGroupId, "Löschfahrzeuge (aktualisiert)", "Neue Beschreibung"))
                .thenReturn(vehicleGroup);

        // Act & Assert
        mockMvc.perform(put("/api/v1/vehicle-groups/{id}", vehicleGroupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Löschfahrzeuge (aktualisiert)", "beschreibung": "Neue Beschreibung"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Löschfahrzeuge (aktualisiert)"));
    }

    @Test
    void updateVehicleGroup_returns404WhenNotFound() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupServiceStub.update(vehicleGroupId, "Name", "Beschreibung"))
                .thenThrow(new VehicleGroupNotFoundException(vehicleGroupId));

        // Act & Assert
        mockMvc.perform(put("/api/v1/vehicle-groups/{id}", vehicleGroupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Name", "beschreibung": "Beschreibung"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(vehicleGroupId.toString())));
    }

    @Test
    void deleteVehicleGroup_returnsNoContent() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleServiceStub.hasNonArchivedVehiclesInGroup(vehicleGroupId)).thenReturn(false);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/vehicle-groups/{id}", vehicleGroupId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVehicleGroup_returns404WhenNotFound() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleServiceStub.hasNonArchivedVehiclesInGroup(vehicleGroupId)).thenReturn(false);
        doThrow(new VehicleGroupNotFoundException(vehicleGroupId))
                .when(vehicleGroupServiceStub).deleteByIdWithConflictCheck(vehicleGroupId, false);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/vehicle-groups/{id}", vehicleGroupId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(vehicleGroupId.toString())));
    }

    @Test
    void deleteVehicleGroup_returns409WhenHasNonArchivedVehicles() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleServiceStub.hasNonArchivedVehiclesInGroup(vehicleGroupId)).thenReturn(true);
        doThrow(new VehicleGroupConflictException(
                "VehicleGroup with id " + vehicleGroupId + " cannot be deleted because it still has non-archived vehicles"))
                .when(vehicleGroupServiceStub).deleteByIdWithConflictCheck(vehicleGroupId, true);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/vehicle-groups/{id}", vehicleGroupId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("non-archived vehicles")));
    }
}
