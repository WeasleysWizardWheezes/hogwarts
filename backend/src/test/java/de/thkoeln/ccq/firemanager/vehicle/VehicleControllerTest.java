package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(VehicleController.class)
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VehicleService vehicleServiceStub;

    @Test
    void createVehicle_returnsCreated() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleServiceStub.create(
                eq("LF 10/6"), eq("Florian Köln 1-46-1"), eq("K-AB 1234"),
                eq(2020), eq("Löschgruppenfahrzeug"), eq(VehicleStatus.VERFUEGBAR), eq(vehicleGroup.getId())))
                .thenReturn(vehicle);

        // Act & Assert
        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "LF 10/6",
                                    "funkrufname": "Florian Köln 1-46-1",
                                    "kennzeichen": "K-AB 1234",
                                    "baujahr": 2020,
                                    "beschreibung": "Löschgruppenfahrzeug",
                                    "status": "VERFUEGBAR",
                                    "vehicleGroupId": "%s"
                                }
                                """.formatted(vehicleGroup.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(vehicle.getId().toString()))
                .andExpect(jsonPath("$.name").value("LF 10/6"))
                .andExpect(jsonPath("$.funkrufname").value("Florian Köln 1-46-1"))
                .andExpect(jsonPath("$.kennzeichen").value("K-AB 1234"))
                .andExpect(jsonPath("$.status").value("VERFUEGBAR"));
    }

    @Test
    void createVehicle_returns400WhenNameIsMissing() throws Exception {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "funkrufname": "Florian Köln 1-46-1",
                                    "kennzeichen": "K-AB 1234",
                                    "vehicleGroupId": "%s"
                                }
                                """.formatted(vehicleGroupId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createVehicle_returns400WhenVehicleGroupIdIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "LF 10/6",
                                    "funkrufname": "Florian Köln 1-46-1",
                                    "kennzeichen": "K-AB 1234"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getVehicle_returnsOk() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleServiceStub.getById(vehicle.getId())).thenReturn(vehicle);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicles/{id}", vehicle.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(vehicle.getId().toString()))
                .andExpect(jsonPath("$.name").value("LF 10/6"))
                .andExpect(jsonPath("$.vehicleGroupId").value(vehicleGroup.getId().toString()))
                .andExpect(jsonPath("$.vehicleGroupName").value("Löschfahrzeuge"));
    }

    @Test
    void getVehicle_returns404WhenNotFound() throws Exception {
        // Arrange
        var vehicleId = UUID.randomUUID();
        when(vehicleServiceStub.getById(vehicleId))
                .thenThrow(new VehicleNotFoundException(vehicleId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(vehicleId.toString())));
    }

    @Test
    void listVehicles_returnsOk() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        var page = new PageImpl<>(List.of(vehicle), PageRequest.of(0, 20), 1);
        when(vehicleServiceStub.getAll(0, 20, null, null)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void updateVehicle_returnsOk() throws Exception {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicleId = UUID.randomUUID();
        var vehicle = new Vehicle("LF 20/16", "Florian Köln 1-46-2", "K-CD 5678",
                2022, "Neues Fahrzeug", VehicleStatus.WARTUNG, vehicleGroup);
        when(vehicleServiceStub.update(eq(vehicleId), eq("LF 20/16"), eq("Florian Köln 1-46-2"),
                eq("K-CD 5678"), eq(2022), eq("Neues Fahrzeug"), eq(VehicleStatus.WARTUNG), isNull()))
                .thenReturn(vehicle);

        // Act & Assert
        mockMvc.perform(put("/api/v1/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "LF 20/16",
                                    "funkrufname": "Florian Köln 1-46-2",
                                    "kennzeichen": "K-CD 5678",
                                    "baujahr": 2022,
                                    "beschreibung": "Neues Fahrzeug",
                                    "status": "WARTUNG"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("LF 20/16"))
                .andExpect(jsonPath("$.status").value("WARTUNG"));
    }

    @Test
    void updateVehicle_returns404WhenNotFound() throws Exception {
        // Arrange
        var vehicleId = UUID.randomUUID();
        when(vehicleServiceStub.update(eq(vehicleId), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new VehicleNotFoundException(vehicleId));

        // Act & Assert
        mockMvc.perform(put("/api/v1/vehicles/{id}", vehicleId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "LF 20/16"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(vehicleId.toString())));
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
        doThrow(new VehicleNotFoundException(vehicleId))
                .when(vehicleServiceStub).deleteById(vehicleId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/vehicles/{id}", vehicleId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(vehicleId.toString())));
    }
}
