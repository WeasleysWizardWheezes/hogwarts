package de.thkoeln.ccq.firemanager.location;

import de.thkoeln.ccq.firemanager.location.exception.LocationNotFoundException;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(LocationController.class)
class LocationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LocationService locationServiceStub;

    @Test
    void createLocation_returnsCreated() throws Exception {
        // Arrange
        UUID locationId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        location.getId(); // Ensure ID is set
        when(locationServiceStub.create("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION"))
                .thenReturn(location);

        String requestBody = """
            {
                "name": "Gerätehaus Köln",
                "address": "Musterstraße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;

        // Act & Assert
        mockMvc.perform(post("/api/v1/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(location.getId().toString()))
                .andExpect(jsonPath("$.name").value("Gerätehaus Köln"))
                .andExpect(jsonPath("$.address").value("Musterstraße 1, 50677 Köln"))
                .andExpect(jsonPath("$.type").value("FIRE_STATION"));
    }


    @Test
    void getLocation_returnsOkWhenLocationExists() throws Exception {
        // Arrange
        UUID locationId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        location.getId(); // Ensure ID is set
        when(locationServiceStub.getById(locationId)).thenReturn(location);

        // Act & Assert
        mockMvc.perform(get("/api/v1/locations/{id}", locationId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(location.getId().toString()))
                .andExpect(jsonPath("$.name").value("Gerätehaus Köln"))
                .andExpect(jsonPath("$.address").value("Musterstraße 1, 50677 Köln"))
                .andExpect(jsonPath("$.type").value("FIRE_STATION"));
    }

    @Test
    void getLocation_returnsNotFoundWhenLocationDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(locationServiceStub.getById(nonExistentId))
                .thenThrow(new LocationNotFoundException(nonExistentId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/locations/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    void listLocations_returnsOk() throws Exception {
        // Arrange
        Location location1 = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        Location location2 = new Location("Gerätehaus Bonn", "Beispielweg 2, 53111 Bonn", "FIRE_STATION");
        when(locationServiceStub.getAll()).thenReturn(List.of(location1, location2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/locations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    void updateLocation_returnsOk() throws Exception {
        // Arrange
        UUID locationId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        location.getId(); // Ensure ID is set
        when(locationServiceStub.update(eq(locationId), any(), any(), any())).thenReturn(location);

        String requestBody = """
            {
                "name": "Gerätehaus Köln-Zentrum",
                "address": "Neue Straße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/v1/locations/{id}", locationId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(location.getId().toString()))
                .andExpect(jsonPath("$.name").value("Gerätehaus Köln"));
    }

    @Test
    void updateLocation_returnsNotFoundWhenLocationDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(locationServiceStub.update(eq(nonExistentId), any(), any(), any()))
                .thenThrow(new LocationNotFoundException(nonExistentId));

        String requestBody = """
            {
                "name": "Gerätehaus Köln-Zentrum",
                "address": "Neue Straße 1, 50677 Köln",
                "type": "FIRE_STATION"
            }
            """;

        // Act & Assert
        mockMvc.perform(put("/api/v1/locations/{id}", nonExistentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteLocation_returnsNoContent() throws Exception {
        // Arrange
        UUID locationId = UUID.randomUUID();
        doNothing().when(locationServiceStub).deleteById(locationId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/locations/{id}", locationId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteLocation_returnsNotFoundWhenLocationDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        doThrow(new LocationNotFoundException(nonExistentId))
                .when(locationServiceStub).deleteById(nonExistentId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/locations/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }
}