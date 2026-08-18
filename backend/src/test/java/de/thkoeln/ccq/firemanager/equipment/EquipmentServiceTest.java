package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentConflictException;
import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentNotFoundException;
import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategoryService;
import de.thkoeln.ccq.firemanager.vehicle.VehicleService;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class EquipmentServiceTest {

    @Mock
    private EquipmentRepository equipmentRepositoryStub;

    @Mock
    private EquipmentHistoryRepository equipmentHistoryRepositoryStub;

    @Mock
    private EquipmentCategoryService equipmentCategoryServiceStub;

    @Mock
    private VehicleService vehicleServiceStub;

    @InjectMocks
    private EquipmentService sut;

    @Test
    void create_defaultsToVerfuegbarAndLoadsCategory() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", "Atemschutzgeräte");
        when(equipmentCategoryServiceStub.getById(category.getId())).thenReturn(category);
        when(equipmentRepositoryStub.existsByInventoryNumber("AGT-2024-0042")).thenReturn(false);
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = sut.create(
                "Pressluftatmer PA 300",
                "AGT-2024-0042",
                "300 bar",
                null,
                category.getId(),
                null,
                null,
                null
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(EquipmentStatus.VERFUEGBAR);
        assertThat(result.getCategory().getId()).isEqualTo(category.getId());
        assertThat(result.getVehicle()).isNull();
    }

    @Test
    void create_throwsConflictWhenInventoryNumberExists() {
        // Arrange
        when(equipmentRepositoryStub.existsByInventoryNumber("AGT-2024-0042")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> sut.create(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, UUID.randomUUID(), null, null, null))
                .isInstanceOf(EquipmentConflictException.class)
                .hasMessageContaining("AGT-2024-0042");
    }

    @Test
    void getAll_delegatesCombinedFiltersAndPagination() {
        // Arrange
        var categoryId = UUID.randomUUID();
        var vehicleId = UUID.randomUUID();
        var dueBefore = LocalDate.of(2026, 6, 30);
        var page = new PageImpl<Equipment>(List.of());
        when(equipmentRepositoryStub.findAllWithFilters(
                "Pressluft", categoryId, vehicleId, EquipmentStatus.WARTUNG,
                dueBefore, PageRequest.of(1, 10))).thenReturn(page);

        // Act
        var result = sut.getAll(
                1, 10, "Pressluft", categoryId, vehicleId,
                EquipmentStatus.WARTUNG, dueBefore);

        // Assert
        assertThat(result).isSameAs(page);
        verify(equipmentRepositoryStub).findAllWithFilters(
                "Pressluft", categoryId, vehicleId, EquipmentStatus.WARTUNG,
                dueBefore, PageRequest.of(1, 10));
    }

    @Test
    void getById_throwsNotFoundForArchivedEquipment() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        equipment.setArchived(true);
        when(equipmentRepositoryStub.findById(equipment.getId())).thenReturn(Optional.of(equipment));

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(equipment.getId()))
                .isInstanceOf(EquipmentNotFoundException.class)
                .hasMessageContaining(equipment.getId().toString());
    }

    @Test
    void update_persistsHistoryWhenStatusChanges() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        when(equipmentRepositoryStub.findById(equipment.getId())).thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = sut.update(
                equipment.getId(), null, "AGT-2024-0042", null,
                EquipmentStatus.WARTUNG, null, null, false,
                null, false, null, false
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(EquipmentStatus.WARTUNG);
        verify(equipmentHistoryRepositoryStub).save(any(EquipmentHistory.class));
    }

    @Test
    void update_doesNotPersistHistoryWhenStatusIsUnchanged() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        when(equipmentRepositoryStub.findById(equipment.getId())).thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        sut.update(
                equipment.getId(), null, null, "Aktualisierte Beschreibung",
                EquipmentStatus.VERFUEGBAR, null, null, false,
                null, false, null, false
        );

        // Assert
        verify(equipmentHistoryRepositoryStub, never()).save(any(EquipmentHistory.class));
    }

    @Test
    void deleteById_archivesEquipmentAndRecordsHistory() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        when(equipmentRepositoryStub.findById(equipment.getId())).thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        sut.deleteById(equipment.getId());

        // Assert
        assertThat(equipment.isArchived()).isTrue();
        assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.ARCHIVIERT);
        verify(equipmentHistoryRepositoryStub).save(any(EquipmentHistory.class));
        verify(equipmentRepositoryStub).save(equipment);
    }

    @Test
    void getHistory_returnsChronologicalHistoryForExistingEquipment() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        var first = new EquipmentHistory(equipment, EquipmentStatus.VERFUEGBAR, EquipmentStatus.WARTUNG);
        var second = new EquipmentHistory(equipment, EquipmentStatus.WARTUNG, EquipmentStatus.VERFUEGBAR);
        when(equipmentRepositoryStub.findById(equipment.getId())).thenReturn(Optional.of(equipment));
        when(equipmentHistoryRepositoryStub.findByEquipmentIdOrderByChangedAtAsc(equipment.getId()))
                .thenReturn(List.of(first, second));

        // Act
        var result = sut.getHistory(equipment.getId());

        // Assert
        assertThat(result).containsExactly(first, second);
    }
}