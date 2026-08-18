package de.thkoeln.ccq.firemanager.equipmentcategory;

import de.thkoeln.ccq.firemanager.equipmentcategory.exception.EquipmentCategoryConflictException;
import de.thkoeln.ccq.firemanager.equipmentcategory.exception.EquipmentCategoryNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Tag("unit")
@WebMvcTest(EquipmentCategoryController.class)
class EquipmentCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EquipmentCategoryService equipmentCategoryServiceStub;

    // ─── POST /api/v1/equipment-categories ──────────────────────────────────

    @Test
    void createCategory_returnsCreated() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", "Atemschutzgeräte");
        when(equipmentCategoryServiceStub.create("Atemschutz", "Atemschutzgeräte")).thenReturn(category);

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Atemschutz", "description": "Atemschutzgeräte"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Atemschutz"))
                .andExpect(jsonPath("$.description").value("Atemschutzgeräte"))
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    void createCategory_withDuplicateName_returnsConflict() throws Exception {
        // Arrange
        when(equipmentCategoryServiceStub.create(eq("Atemschutz"), any()))
                .thenThrow(new EquipmentCategoryConflictException("already exists"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Atemschutz"}
                                """))
                .andExpect(status().isConflict());
    }

    @Test
    void createCategory_withMissingName_returnsBadRequest() throws Exception {
        // Act & Assert
        mockMvc.perform(post("/api/v1/equipment-categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description": "Nur Beschreibung"}
                                """))
                .andExpect(status().isBadRequest());
    }

    // ─── GET /api/v1/equipment-categories ───────────────────────────────────

    @Test
    void listCategories_returnsOk() throws Exception {
        // Arrange
        var cat = new EquipmentCategory("Atemschutz", null);
        var page = new PageImpl<>(List.of(cat));
        when(equipmentCategoryServiceStub.getAll(0, 20, null)).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment-categories?page=0&size=20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Atemschutz"))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    // ─── GET /api/v1/equipment-categories/{id} ──────────────────────────────

    @Test
    void getCategory_returnsOk() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", "Beschreibung");
        when(equipmentCategoryServiceStub.getById(category.getId())).thenReturn(category);

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment-categories/" + category.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Atemschutz"));
    }

    @Test
    void getCategory_returnsNotFound() throws Exception {
        // Arrange
        var id = UUID.randomUUID();
        when(equipmentCategoryServiceStub.getById(id))
                .thenThrow(new EquipmentCategoryNotFoundException(id));

        // Act & Assert
        mockMvc.perform(get("/api/v1/equipment-categories/" + id))
                .andExpect(status().isNotFound());
    }

    // ─── PUT /api/v1/equipment-categories/{id} ──────────────────────────────

    @Test
    void updateCategory_returnsOk() throws Exception {
        // Arrange
        var category = new EquipmentCategory("Funk", "Funkgeräte");
        when(equipmentCategoryServiceStub.update(category.getId(), "Funk", "Funkgeräte"))
                .thenReturn(category);

        // Act & Assert
        mockMvc.perform(put("/api/v1/equipment-categories/" + category.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Funk", "description": "Funkgeräte"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Funk"));
    }

    @Test
    void updateCategory_returnsNotFound() throws Exception {
        // Arrange
        var id = UUID.randomUUID();
        when(equipmentCategoryServiceStub.update(eq(id), any(), any()))
                .thenThrow(new EquipmentCategoryNotFoundException(id));

        // Act & Assert
        mockMvc.perform(put("/api/v1/equipment-categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Funk"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateCategory_withDuplicateName_returnsConflict() throws Exception {
        // Arrange
        var id = UUID.randomUUID();
        when(equipmentCategoryServiceStub.update(eq(id), eq("Atemschutz"), any()))
                .thenThrow(new EquipmentCategoryConflictException("already exists"));

        // Act & Assert
        mockMvc.perform(put("/api/v1/equipment-categories/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Atemschutz"}
                                """))
                .andExpect(status().isConflict());
    }

    // ─── DELETE /api/v1/equipment-categories/{id} ────────────────────────────

    @Test
    void deleteCategory_returnsNoContent() throws Exception {
        // Arrange
        var id = UUID.randomUUID();

        // Act & Assert
        mockMvc.perform(delete("/api/v1/equipment-categories/" + id))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteCategory_returnsNotFound() throws Exception {
        // Arrange
        var id = UUID.randomUUID();
        doThrow(new EquipmentCategoryNotFoundException(id))
                .when(equipmentCategoryServiceStub).deleteById(id);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/equipment-categories/" + id))
                .andExpect(status().isNotFound());
    }
}
