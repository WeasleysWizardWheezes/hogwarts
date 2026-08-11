package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
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
@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehicleService vehicleServiceStub;

    @Test
    void create_returnsCreated() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleServiceStub.create("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup.getId())).thenReturn(vehicle);

        // Act & Assert
        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "LF 10/6", "radioCallName": "Funk-01", "licensePlate": "M-AB1234",
                                 "yearOfConstruction": 2020, "description": "Desc", "status": "VERFUEGBAR",
                                 "vehicleGroupId": "%s"}
                                """.formatted(vehicleGroup.getId())))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name").value("LF 10/6"));
    }

    @Test
    void create_returns400WhenNameIsBlank() throws Exception {
        // Act & Assert – kein Stubbing nötig (Validierung greift vor Service-Aufruf)
        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "radioCallName": "Funk-01", "licensePlate": "M-AB1234",
                                 "yearOfConstruction": 2020, "description": "Desc", "status": "VERFUEGBAR",
                                 "vehicleGroupId": "00000000-0000-0000-0000-000000000001"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void create_returns400WhenStatusIsNull() throws Exception {
        // Act & Assert – kein Stubbing nötig (Validierung greift vor Service-Aufruf)
        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "LF 10/6", "radioCallName": "Funk-01", "licensePlate": "M-AB1234",
                                 "yearOfConstruction": 2020, "description": "Desc", "status": null,
                                 "vehicleGroupId": "00000000-0000-0000-0000-000000000001"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAll_returnsOk() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle1 = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        var vehicle2 = new Vehicle("LF 20/16", "Funk-02", "M-CD5678", 2018, "Desc",
                Vehicle.VehicleStatus.WARTUNG, vehicleGroup);
        when(vehicleServiceStub.getAll()).thenReturn(List.of(vehicle1, vehicle2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getVehicleById_returnsOk() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleServiceStub.getById(vehicle.getId())).thenReturn(vehicle);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicles/{id}", vehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("LF 10/6"));
    }

    @Test
    void getVehicleById_returns404WhenNotFound() throws Exception {
        // Arrange
        var vehicleId = UUID.randomUUID();
        when(vehicleServiceStub.getById(vehicleId))
                .thenThrow(new de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException(vehicleId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(vehicleId.toString())));
    }

    @Test
    void update_returnsOk() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6 (aktualisiert)", "Funk-01-updated", "M-AB1234", 2020, "Neue Desc",
                Vehicle.VehicleStatus.WARTUNG, vehicleGroup);
        var vehicleId = UUID.randomUUID();
        when(vehicleServiceStub.update(vehicleId, "LF 10/6 (aktualisiert)", "Funk-01-updated", "M-AB1234", 2020, "Neue Desc",
                Vehicle.VehicleStatus.WARTUNG, vehicleGroup.getId())).thenReturn(vehicle);

        // Act & Assert
        mockMvc.perform(put("/api/v1/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "LF 10/6 (aktualisiert)", "radioCallName": "Funk-01-updated", "licensePlate": "M-AB1234",
                                 "yearOfConstruction": 2020, "description": "Neue Desc", "status": "WARTUNG",
                                 "vehicleGroupId": "%s"}
                                """.formatted(vehicleGroup.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("LF 10/6 (aktualisiert)"));
    }

    @Test
    void deleteVehicle_returnsNoContent() throws Exception {
        // Arrange
        var vehicleId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteVehicle_returns404WhenNotFound() throws Exception {
        // Arrange
        var vehicleId = UUID.randomUUID();
        // doThrow ist bei void-Methoden nicht nötig - default tut nichts, aber Controller wirft NotFoundException
        doThrow(new de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException(vehicleId))
                .when(vehicleServiceStub).deleteById(vehicleId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteVehicle_returns409WhenInUse() throws Exception {
        // Arrange
        var vehicleId = UUID.randomUUID();
        doThrow(new de.thkoeln.ccq.firemanager.vehicle.exception.VehicleInUseException())
                .when(vehicleServiceStub).deleteById(vehicleId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("cannot be archived while in use")));
    }
}