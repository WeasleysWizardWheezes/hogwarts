package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentConflictException;
import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentNotFoundException;
import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
import de.thkoeln.ccq.firemanager.equipmentcategory.exception.EquipmentCategoryNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.openapitools.jackson.nullable.JsonNullableModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(EquipmentController.class)
class EquipmentControllerTest {

    @TestConfiguration
    static class JacksonConfig {
        @Bean
        public JsonNullableModule jsonNullableModule() {
            return new JsonNullableModule();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EquipmentService equipmentServiceStub;

    private Equipment buildEquipment(String name, String inventoryNumber) {
        var category = new EquipmentCategory("Atemschutz", null);
        return new Equipment(name, inventoryNumber, "Beschreibung",
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
    }

    private Equipment buildEquipmentWithVehicle(String name, String inventoryNumber) {
        var category = new EquipmentCategory("Atemschutz", null);
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        return new Equipment(name, inventoryNumber, "Beschreibung",
                EquipmentStatus.VERFUEGBAR, category, vehicle, null, null);
    }

    // ─── POST /api/v1/equipment ───────────────────────────────────────────────

    @Test
    void createEquipment_returnsCreated() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = buildEquipment("Atemschutzgerät PA 300", "INV-001");
        when(equipmentServiceStub.create(
                eq("Atemschutzgerät PA 300"), eq("INV-001"), eq("Beschreibung"),
                eq(EquipmentStatus.VERFUEGBAR), eq(category.getId()),
                isNull(), isNull(), isNull()))
                .thenReturn(equipment);

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Atemschutzgerät PA 300",
                                    "inventoryNumber": "INV-001",
                                    "description": "Beschreibung",
                                    "status": "VERFUEGBAR",
                                    "categoryId": "%s"
                                }
                                """.formatted(category.getId())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/api/v1/equipment/")))
                .andExpect(jsonPath("$.id").value(equipment.getId().toString()))
                .andExpect(jsonPath("$.name").value("Atemschutzgerät PA 300"))
                .andExpect(jsonPath("$.inventoryNumber").value("INV-001"))
                .andExpect(jsonPath("$.status").value("VERFUEGBAR"));
    }

    @Test
    void createEquipment_returns400WhenNameIsMissing() throws Exception {
        // Arrange
        var categoryId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "inventoryNumber": "INV-001",
                                    "categoryId": "%s"
                                }
                                """.formatted(categoryId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEquipment_returns400WhenInventoryNumberIsMissing() throws Exception {
        // Arrange
        var categoryId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Atemschutzgerät PA 300",
                                    "categoryId": "%s"
                                }
                                """.formatted(categoryId)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEquipment_returns400WhenCategoryIdIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Atemschutzgerät PA 300",
                                    "inventoryNumber": "INV-001"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEquipment_returns409WhenInventoryNumberExists() throws Exception {
        // Arrange
        var categoryId = UUID.randomUUID();
        when(equipmentServiceStub.create(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new EquipmentConflictException("Equipment with inventoryNumber 'INV-001' already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Atemschutzgerät PA 300",
                                    "inventoryNumber": "INV-001",
                                    "categoryId": "%s"
                                }
                                """.formatted(categoryId)))
                .andExpect(status().isConflict());
    }

    @Test
    void createEquipment_returns404WhenCategoryNotFound() throws Exception {
        // Arrange
        var categoryId = UUID.randomUUID();
        when(equipmentServiceStub.create(any(), any(), any(), any(), eq(categoryId), any(), any(), any()))
                .thenThrow(new EquipmentCategoryNotFoundException(categoryId));

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Atemschutzgerät PA 300",
                                    "inventoryNumber": "INV-002",
                                    "categoryId": "%s"
                                }
                                """.formatted(categoryId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void createEquipment_returns404WhenVehicleNotFound() throws Exception {
        // Arrange
        var categoryId = UUID.randomUUID();
        // vehicleId wird nicht im Body gesendet (JsonNullable-Deserialisierung nicht im WebMvc-Slice)
        // Service-Stub wirft trotzdem VehicleNotFoundException, um das HTTP-Mapping zu prüfen
        when(equipmentServiceStub.create(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new VehicleNotFoundException(UUID.randomUUID()));

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Atemschutzgerät PA 300",
                                    "inventoryNumber": "INV-003",
                                    "categoryId": "%s"
                                }
                                """.formatted(categoryId)))
                .andExpect(status().isNotFound());
    }

    // ─── GET /api/v1/equipment ────────────────────────────────────────────────

    @Test
    void listEquipment_returnsOk() throws Exception {
        // Arrange
        var equipment = buildEquipment("Atemschutzgerät PA 300", "INV-010");
        var page = new PageImpl<>(List.of(equipment), PageRequest.of(0, 20), 1);
        when(equipmentServiceStub.getAll(0, 20, null, null, null, null, null)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].name").value("Atemschutzgerät PA 300"))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.page").value(0))
                .andExpect(jsonPath("$.page.size").value(20));
    }

    @Test
    void listEquipment_withSearchParam_returnsOk() throws Exception {
        // Arrange
        var equipment = buildEquipment("Atemschutzgerät PA 300", "INV-011");
        var page = new PageImpl<>(List.of(equipment), PageRequest.of(0, 20), 1);
        when(equipmentServiceStub.getAll(0, 20, "Atemschutz", null, null, null, null)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment?search=Atemschutz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    @Test
    void listEquipment_withStatusFilter_returnsOk() throws Exception {
        // Arrange
        var page = new PageImpl<Equipment>(List.of(), PageRequest.of(0, 20), 0);
        when(equipmentServiceStub.getAll(0, 20, null, null, null, EquipmentStatus.DEFEKT, null)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment?status=DEFEKT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void listEquipment_withDueBefore_returnsOk() throws Exception {
        // Arrange
        var equipment = buildEquipment("Gerät mit fälligem Termin", "INV-012");
        var page = new PageImpl<>(List.of(equipment), PageRequest.of(0, 20), 1);
        when(equipmentServiceStub.getAll(0, 20, null, null, null, null, LocalDate.of(2025, 12, 31)))
                .thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment?dueBefore=2025-12-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1));
    }

    // ─── GET /api/v1/equipment/{id} ───────────────────────────────────────────

    @Test
    void getEquipment_returnsOk() throws Exception {
        // Arrange
        var equipment = buildEquipment("Atemschutzgerät PA 300", "INV-020");
        when(equipmentServiceStub.getById(equipment.getId())).thenReturn(equipment);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment/{id}", equipment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equipment.getId().toString()))
                .andExpect(jsonPath("$.name").value("Atemschutzgerät PA 300"))
                .andExpect(jsonPath("$.inventoryNumber").value("INV-020"))
                .andExpect(jsonPath("$.status").value("VERFUEGBAR"));
    }

    @Test
    void getEquipment_returnsVehicleInfoWhenAssigned() throws Exception {
        // Arrange
        var equipment = buildEquipmentWithVehicle("Atemschutzgerät PA 300", "INV-021");
        when(equipmentServiceStub.getById(equipment.getId())).thenReturn(equipment);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment/{id}", equipment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").isNotEmpty());
    }

    @Test
    void getEquipment_returns404WhenNotFound() throws Exception {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentServiceStub.getById(equipmentId))
                .thenThrow(new EquipmentNotFoundException(equipmentId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment/{id}", equipmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(equipmentId.toString())));
    }

    // ─── PUT /api/v1/equipment/{id} ───────────────────────────────────────────

    @Test
    void updateEquipment_returnsOk() throws Exception {
        // Arrange
        var equipment = buildEquipment("Atemschutzgerät PA 500", "INV-030");
        var equipmentId = UUID.randomUUID();
        when(equipmentServiceStub.update(
                eq(equipmentId), eq("Atemschutzgerät PA 500"), eq("INV-030"),
                any(), any(), any(), any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean()))
                .thenReturn(equipment);

        // Act & Assert
        mockMvc.perform(put("/api/v1/equipment/{id}", equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Atemschutzgerät PA 500",
                                    "inventoryNumber": "INV-030"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Atemschutzgerät PA 500"));
    }

    @Test
    void updateEquipment_returns404WhenNotFound() throws Exception {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentServiceStub.update(eq(equipmentId), any(), any(), any(), any(), any(),
                any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean()))
                .thenThrow(new EquipmentNotFoundException(equipmentId));

        // Act & Assert
        mockMvc.perform(put("/api/v1/equipment/{id}", equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Atemschutzgerät PA 500"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(equipmentId.toString())));
    }

    @Test
    void updateEquipment_returns409WhenInventoryNumberConflict() throws Exception {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentServiceStub.update(eq(equipmentId), any(), any(), any(), any(), any(),
                any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean()))
                .thenThrow(new EquipmentConflictException("Equipment with inventoryNumber 'INV-999' already exists"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/equipment/{id}", equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"inventoryNumber": "INV-999"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void updateEquipment_returns404WhenCategoryNotFound() throws Exception {
        // Arrange
        var equipmentId = UUID.randomUUID();
        var categoryId = UUID.randomUUID();
        when(equipmentServiceStub.update(eq(equipmentId), any(), any(), any(), any(), eq(categoryId),
                any(), anyBoolean(), any(), anyBoolean(), any(), anyBoolean()))
                .thenThrow(new EquipmentCategoryNotFoundException(categoryId));

        // Act & Assert
        mockMvc.perform(put("/api/v1/equipment/{id}", equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"categoryId": "%s"}
                                """.formatted(categoryId)))
                .andExpect(status().isNotFound());
    }

    // ─── DELETE /api/v1/equipment/{id} ───────────────────────────────────────

    @Test
    void deleteEquipment_returnsNoContent() throws Exception {
        // Arrange
        var equipmentId = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/v1/equipment/{id}", equipmentId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteEquipment_returns404WhenNotFound() throws Exception {
        // Arrange
        var equipmentId = UUID.randomUUID();
        doThrow(new EquipmentNotFoundException(equipmentId))
                .when(equipmentServiceStub).deleteById(equipmentId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/equipment/{id}", equipmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(equipmentId.toString())));
    }

    // ─── GET /api/v1/equipment/{id}/history ──────────────────────────────────

    @Test
    void getEquipmentHistory_returnsOk() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment("Atemschutzgerät PA 300", "INV-050", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        var history1 = new EquipmentHistory(equipment, null, EquipmentStatus.VERFUEGBAR);
        var history2 = new EquipmentHistory(equipment, EquipmentStatus.VERFUEGBAR, EquipmentStatus.DEFEKT);
        when(equipmentServiceStub.getById(equipment.getId())).thenReturn(equipment);
        when(equipmentServiceStub.getHistory(equipment.getId())).thenReturn(List.of(history1, history2));

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment/{id}/history", equipment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].newStatus").value("VERFUEGBAR"))
                .andExpect(jsonPath("$[1].newStatus").value("DEFEKT"));
    }

    @Test
    void getEquipmentHistory_returns404WhenEquipmentNotFound() throws Exception {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentServiceStub.getHistory(equipmentId))
                .thenThrow(new EquipmentNotFoundException(equipmentId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment/{id}/history", equipmentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(containsString(equipmentId.toString())));
    }

    @Test
    void getEquipmentHistory_returnsEmptyListWhenNoHistory() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment("Atemschutzgerät PA 300", "INV-051", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        when(equipmentServiceStub.getHistory(equipment.getId())).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment/{id}/history", equipment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
