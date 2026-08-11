package de.thkoeln.ccq.firemanager.vehiclegroup;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(VehicleGroupController.class)
class VehicleGroupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleGroupService vehicleGroupServiceStub;

    @Test
    void create_returnsCreated() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupServiceStub.create("Löschfahrzeuge", "Beschreibung")).thenReturn(vehicleGroup);

        // Act & Assert
        mockMvc.perform(post("/api/v1/vehicle-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Löschfahrzeuge", "description": "Beschreibung"}
                                """))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("Löschfahrzeuge"));
    }

    @Test
    void create_returns400WhenNameIsBlank() throws Exception {
        // Act & Assert – kein Stubbing nötig (Validierung greift vor Service-Aufruf)
        mockMvc.perform(post("/api/v1/vehicle-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "description": "Beschreibung"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns409WhenNameExists() throws Exception {
        // Arrange
        when(vehicleGroupServiceStub.create("Löschfahrzeuge", "Beschreibung"))
                .thenThrow(new de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupConflictException("VehicleGroup with name Löschfahrzeuge already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/vehicle-groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Löschfahrzeuge", "description": "Beschreibung"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void getAll_returnsOk() throws Exception {
        // Arrange
        var group1 = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var group2 = new VehicleGroup("Rettungsdienst", "Beschreibung");
        when(vehicleGroupServiceStub.getAll()).thenReturn(List.of(group1, group2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicle-groups"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getById_returnsOk() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupServiceStub.getById(vehicleGroup.getId())).thenReturn(vehicleGroup);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicle-groups/{id}", vehicleGroup.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Löschfahrzeuge"));
    }

    @Test
    void getById_returns404WhenNotFound() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupServiceStub.getById(vehicleGroupId))
                .thenThrow(new de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException(vehicleGroupId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicle-groups/{id}", vehicleGroupId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(vehicleGroupId.toString())));
    }

    @Test
    void update_returnsOk() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge (aktualisiert)", "Neue Beschreibung");
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupServiceStub.update(vehicleGroupId, "Löschfahrzeuge (aktualisiert)", "Neue Beschreibung")).thenReturn(vehicleGroup);

        // Act & Assert
        mockMvc.perform(put("/api/v1/vehicle-groups/{id}", vehicleGroupId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Löschfahrzeuge (aktualisiert)", "description": "Neue Beschreibung"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Löschfahrzeuge (aktualisiert)"));
    }

    @Test
    void delete_returnsNoContent() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/v1/vehicle-groups/{id}", vehicleGroupId))
                .andExpect(status().isNoContent());
    }

    @Test
    void delete_returns404WhenNotFound() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        // doThrow ist bei void-Methoden nicht nötig - default tut nichts, aber Controller wirft NotFoundException
        // Da deleteById void ist und NotFoundException throwt, muss der Controller das abfangen
        // Das funktioniert nur wenn der Service die Exception wirft - hier wird deleteById direkt aufgerufen
        doThrow(new de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException(vehicleGroupId))
                .when(vehicleGroupServiceStub).deleteById(vehicleGroupId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/vehicle-groups/{id}", vehicleGroupId))
                .andExpect(status().isNotFound());
    }
}