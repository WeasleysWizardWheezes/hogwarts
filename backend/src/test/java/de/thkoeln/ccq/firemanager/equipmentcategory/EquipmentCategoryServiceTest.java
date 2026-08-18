package de.thkoeln.ccq.firemanager.equipmentcategory;

import de.thkoeln.ccq.firemanager.equipmentcategory.exception.EquipmentCategoryConflictException;
import de.thkoeln.ccq.firemanager.equipmentcategory.exception.EquipmentCategoryNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class EquipmentCategoryServiceTest {

    @Mock
    private EquipmentCategoryRepository equipmentCategoryRepositoryStub;

    @InjectMocks
    private EquipmentCategoryService sut;

    // ─── create ─────────────────────────────────────────────────────────────

    @Test
    void create_returnsCategory() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", "Atemschutzgeräte und Zubehör");
        when(equipmentCategoryRepositoryStub.existsByNameAndArchivedFalse("Atemschutz")).thenReturn(false);
        when(equipmentCategoryRepositoryStub.save(any(EquipmentCategory.class))).thenReturn(category);

        // Act
        var result = sut.create("Atemschutz", "Atemschutzgeräte und Zubehör");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Atemschutz");
        assertThat(result.getDescription()).isEqualTo("Atemschutzgeräte und Zubehör");
    }

    @Test
    void create_withDuplicateName_throwsConflict() {
        // Arrange
        when(equipmentCategoryRepositoryStub.existsByNameAndArchivedFalse("Atemschutz")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> sut.create("Atemschutz", "Beschreibung"))
                .isInstanceOf(EquipmentCategoryConflictException.class);
    }

    // ─── getAll ─────────────────────────────────────────────────────────────

    @Test
    void getAll_returnsPageOfCategories() {
        // Arrange
        var cat1 = new EquipmentCategory("Atemschutz", null);
        var cat2 = new EquipmentCategory("Funk", null);
        Page<EquipmentCategory> page = new PageImpl<>(List.of(cat1, cat2));
        when(equipmentCategoryRepositoryStub.findAllWithSearch(null, PageRequest.of(0, 20))).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, null);

        // Assert
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void getAll_passesSearchParameterToRepository() {
        // Arrange
        var cat = new EquipmentCategory("Atemschutz", null);
        Page<EquipmentCategory> page = new PageImpl<>(List.of(cat));
        when(equipmentCategoryRepositoryStub.findAllWithSearch("Atem", PageRequest.of(0, 20))).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, "Atem");

        // Assert
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAll_treatsBlankSearchAsNull() {
        // Arrange
        Page<EquipmentCategory> page = new PageImpl<>(List.of());
        when(equipmentCategoryRepositoryStub.findAllWithSearch(null, PageRequest.of(0, 20))).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, "   ");

        // Assert
        assertThat(result.getContent()).isEmpty();
    }

    // ─── getById ────────────────────────────────────────────────────────────

    @Test
    void getById_returnsCategoryWhenFound() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        when(equipmentCategoryRepositoryStub.findById(category.getId())).thenReturn(Optional.of(category));

        // Act
        var result = sut.getById(category.getId());

        // Assert
        assertThat(result.getName()).isEqualTo("Atemschutz");
    }

    @Test
    void getById_throwsNotFoundWhenMissing() {
        // Arrange
        var id = UUID.randomUUID();
        when(equipmentCategoryRepositoryStub.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(id))
                .isInstanceOf(EquipmentCategoryNotFoundException.class);
    }

    @Test
    void getById_throwsNotFoundWhenArchived() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        category.setArchived(true);
        when(equipmentCategoryRepositoryStub.findById(category.getId())).thenReturn(Optional.of(category));

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(category.getId()))
                .isInstanceOf(EquipmentCategoryNotFoundException.class);
    }

    @Test
    void getById_throwsIllegalArgumentWhenNull() {
        // Act & Assert
        assertThatThrownBy(() -> sut.getById(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ─── update ─────────────────────────────────────────────────────────────

    @Test
    void update_returnsCategoryWithNewValues() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", "Alt");
        when(equipmentCategoryRepositoryStub.findById(category.getId())).thenReturn(Optional.of(category));
        when(equipmentCategoryRepositoryStub.existsByNameAndIdNotAndArchivedFalse("Funk", category.getId()))
                .thenReturn(false);
        when(equipmentCategoryRepositoryStub.save(any(EquipmentCategory.class))).thenReturn(category);

        // Act
        var result = sut.update(category.getId(), "Funk", "Neue Beschreibung");

        // Assert
        assertThat(result.getName()).isEqualTo("Funk");
        assertThat(result.getDescription()).isEqualTo("Neue Beschreibung");
    }

    @Test
    void update_withDuplicateName_throwsConflict() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        when(equipmentCategoryRepositoryStub.findById(category.getId())).thenReturn(Optional.of(category));
        when(equipmentCategoryRepositoryStub.existsByNameAndIdNotAndArchivedFalse("Funk", category.getId()))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> sut.update(category.getId(), "Funk", null))
                .isInstanceOf(EquipmentCategoryConflictException.class);
    }

    @Test
    void update_throwsNotFoundWhenMissing() {
        // Arrange
        var id = UUID.randomUUID();
        when(equipmentCategoryRepositoryStub.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.update(id, "Funk", null))
                .isInstanceOf(EquipmentCategoryNotFoundException.class);
    }

    // ─── deleteById ─────────────────────────────────────────────────────────

    @Test
    void deleteById_setsArchivedTrue() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        when(equipmentCategoryRepositoryStub.findById(category.getId())).thenReturn(Optional.of(category));

        // Act
        sut.deleteById(category.getId());

        // Assert
        verify(equipmentCategoryRepositoryStub).save(category);
        assertThat(category.isArchived()).isTrue();
    }

    @Test
    void deleteById_throwsNotFoundWhenMissing() {
        // Arrange
        var id = UUID.randomUUID();
        when(equipmentCategoryRepositoryStub.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.deleteById(id))
                .isInstanceOf(EquipmentCategoryNotFoundException.class);
    }
}
