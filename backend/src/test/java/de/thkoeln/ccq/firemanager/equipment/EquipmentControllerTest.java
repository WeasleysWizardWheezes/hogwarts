package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentConflictException;
import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentNotFoundException;
import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
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

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EquipmentService equipmentServiceStub;

    @Test
    void createEquipment_returnsCreatedWithLocation() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", "Atemschutzgeräte");
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", "300 bar",
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        when(equipmentServiceStub.create(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(equipment);

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Pressluftatmer PA 300",
                                    "inventoryNumber": "AGT-2024-0042",
                                    "description": "300 bar",
                                    "categoryId": "%s"
                                }
                                """.formatted(category.getId())))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        "/api/v1/equipment/" + equipment.getId()))
                .andExpect(jsonPath("$.id").value(equipment.getId().toString()))
                .andExpect(jsonPath("$.name").value("Pressluftatmer PA 300"))
                .andExpect(jsonPath("$.categoryName").value("Atemschutz"));
    }

    @Test
    void createEquipment_returns400WhenRequiredFieldIsMissing() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Pressluftatmer PA 300",
                                    "inventoryNumber": "AGT-2024-0042"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createEquipment_returns409WhenInventoryNumberExists() throws Exception {
        // Arrange
        when(equipmentServiceStub.create(any(), any(), any(), any(), any(), any(), any(), any()))
                .thenThrow(new EquipmentConflictException("inventory number already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Pressluftatmer PA 300",
                                    "inventoryNumber": "AGT-2024-0042",
                                    "categoryId": "%s"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(containsString("already exists")));
    }

    @Test
    void getEquipment_returnsOk() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        when(equipmentServiceStub.getById(equipment.getId())).thenReturn(equipment);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment/{id}", equipment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(equipment.getId().toString()))
                .andExpect(jsonPath("$.status").value("VERFUEGBAR"));
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

    @Test
    void listEquipment_returnsPage() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        var page = new PageImpl<>(List.of(equipment), PageRequest.of(0, 20), 1);
        when(equipmentServiceStub.getAll(0, 20, null, null, null, null, null)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    void updateEquipment_returnsOk() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Pressluftatmer PA 301", "AGT-2024-0043", "Neu",
                EquipmentStatus.WARTUNG, category, null, null, null);
        var equipmentId = UUID.randomUUID();
        when(equipmentServiceStub.update(
                eq(equipmentId), eq("Pressluftatmer PA 301"), eq("AGT-2024-0043"), eq("Neu"),
                eq(EquipmentStatus.WARTUNG), isNull(), isNull(), anyBoolean(),
                isNull(), anyBoolean(), isNull(), anyBoolean())).thenReturn(equipment);

        // Act & Assert
        mockMvc.perform(put("/api/v1/equipment/{id}", equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                    "name": "Pressluftatmer PA 301",
                                    "inventoryNumber": "AGT-2024-0043",
                                    "description": "Neu",
                                    "status": "WARTUNG"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pressluftatmer PA 301"))
                .andExpect(jsonPath("$.status").value("WARTUNG"));
    }

    @Test
    void updateEquipment_returns404WhenNotFound() throws Exception {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentServiceStub.update(eq(equipmentId), any(), any(), any(), any(), any(), any(),
                anyBoolean(), any(), anyBoolean(), any(), anyBoolean()))
                .thenThrow(new EquipmentNotFoundException(equipmentId));

        // Act & Assert
        mockMvc.perform(put("/api/v1/equipment/{id}", equipmentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Pressluftatmer PA 301\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteEquipment_returnsNoContent() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/api/v1/equipment/{id}", UUID.randomUUID()))
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
                .andExpect(status().isNotFound());
    }

    @Test
    void getEquipmentHistory_returnsOk() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        var history = new EquipmentHistory(
                equipment, EquipmentStatus.VERFUEGBAR, EquipmentStatus.WARTUNG);
        when(equipmentServiceStub.getHistory(equipment.getId())).thenReturn(List.of(history));

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment/{id}/history", equipment.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].equipmentId").value(equipment.getId().toString()))
                .andExpect(jsonPath("$[0].newStatus").value("WARTUNG"));
    }

    @Test
    void getEquipmentHistory_returns404WhenNotFound() throws Exception {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentServiceStub.getHistory(equipmentId))
                .thenThrow(new EquipmentNotFoundException(equipmentId));

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment/{id}/history", equipmentId))
                .andExpect(status().isNotFound());
    }
}