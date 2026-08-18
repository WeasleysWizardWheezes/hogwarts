package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentConflictException;
import de.thkoeln.ccq.firemanager.equipment.exception.EquipmentNotFoundException;
import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategoryService;
import de.thkoeln.ccq.firemanager.equipmentcategory.exception.EquipmentCategoryNotFoundException;
import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.VehicleService;
import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
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

    // ─── create ──────────────────────────────────────────────────────────────

    @Test
    void create_returnsEquipmentWithGeneratedId() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", "Atemschutzgeräte");
        when(equipmentCategoryServiceStub.getById(category.getId())).thenReturn(category);

        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-001", "Pressluftatemschutz",
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.save(any(Equipment.class))).thenReturn(equipment);
        when(equipmentHistoryRepositoryStub.save(any(EquipmentHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = sut.create(
                "Atemschutzgerät PA 300", "INV-001", "Pressluftatemschutz",
                EquipmentStatus.VERFUEGBAR, category.getId(), null, null, null
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Atemschutzgerät PA 300");
        assertThat(result.getInventoryNumber()).isEqualTo("INV-001");
        assertThat(result.getStatus()).isEqualTo(EquipmentStatus.VERFUEGBAR);
    }

    @Test
    void create_defaultsToVerfuegbarWhenStatusIsNull() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        when(equipmentCategoryServiceStub.getById(category.getId())).thenReturn(category);

        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-002", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.save(any(Equipment.class))).thenReturn(equipment);
        when(equipmentHistoryRepositoryStub.save(any(EquipmentHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = sut.create(
                "Atemschutzgerät PA 300", "INV-002", null,
                null, category.getId(), null, null, null
        );

        // Assert
        assertThat(result.getStatus()).isEqualTo(EquipmentStatus.VERFUEGBAR);
    }

    @Test
    void create_savesHistoryEntryWithNullPreviousStatus() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        when(equipmentCategoryServiceStub.getById(category.getId())).thenReturn(category);

        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-003", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.save(any(Equipment.class))).thenReturn(equipment);
        when(equipmentHistoryRepositoryStub.save(any(EquipmentHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        sut.create("Atemschutzgerät PA 300", "INV-003", null,
                EquipmentStatus.VERFUEGBAR, category.getId(), null, null, null);

        // Assert
        verify(equipmentHistoryRepositoryStub).save(any(EquipmentHistory.class));
    }

    @Test
    void create_setsVehicleWhenVehicleIdProvided() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle(
                "LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", VehicleStatus.VERFUEGBAR, vehicleGroup
        );
        when(equipmentCategoryServiceStub.getById(category.getId())).thenReturn(category);
        when(vehicleServiceStub.getById(vehicle.getId())).thenReturn(vehicle);

        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-004", null,
                EquipmentStatus.VERFUEGBAR, category, vehicle, null, null
        );
        when(equipmentRepositoryStub.save(any(Equipment.class))).thenReturn(equipment);
        when(equipmentHistoryRepositoryStub.save(any(EquipmentHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = sut.create(
                "Atemschutzgerät PA 300", "INV-004", null,
                EquipmentStatus.VERFUEGBAR, category.getId(), vehicle.getId(), null, null
        );

        // Assert
        assertThat(result.getVehicle()).isNotNull();
        assertThat(result.getVehicle().getId()).isEqualTo(vehicle.getId());
    }

    @Test
    void create_throwsConflictExceptionWhenInventoryNumberExists() {
        // Arrange
        when(equipmentRepositoryStub.existsByInventoryNumberAndArchivedFalse("INV-001"))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> sut.create(
                "Atemschutzgerät PA 300", "INV-001", null,
                null, UUID.randomUUID(), null, null, null
        )).isInstanceOf(EquipmentConflictException.class)
                .hasMessageContaining("INV-001");
    }

    @Test
    void create_throwsNotFoundWhenCategoryNotFound() {
        // Arrange
        var categoryId = UUID.randomUUID();
        when(equipmentRepositoryStub.existsByInventoryNumberAndArchivedFalse(any())).thenReturn(false);
        when(equipmentCategoryServiceStub.getById(categoryId))
                .thenThrow(new EquipmentCategoryNotFoundException(categoryId));

        // Act & Assert
        assertThatThrownBy(() -> sut.create(
                "Atemschutzgerät PA 300", "INV-005", null,
                null, categoryId, null, null, null
        )).isInstanceOf(EquipmentCategoryNotFoundException.class);
    }

    @Test
    void create_throwsNotFoundWhenVehicleNotFound() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var vehicleId = UUID.randomUUID();
        when(equipmentCategoryServiceStub.getById(category.getId())).thenReturn(category);
        when(vehicleServiceStub.getById(vehicleId))
                .thenThrow(new VehicleNotFoundException(vehicleId));

        // Act & Assert
        assertThatThrownBy(() -> sut.create(
                "Atemschutzgerät PA 300", "INV-006", null,
                null, category.getId(), vehicleId, null, null
        )).isInstanceOf(VehicleNotFoundException.class);
    }

    @Test
    void create_storesDatesWhenProvided() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var inspectionDate = LocalDate.of(2025, 6, 1);
        var maintenanceDate = LocalDate.of(2025, 12, 1);
        when(equipmentCategoryServiceStub.getById(category.getId())).thenReturn(category);

        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-007", null,
                EquipmentStatus.VERFUEGBAR, category, null, inspectionDate, maintenanceDate
        );
        when(equipmentRepositoryStub.save(any(Equipment.class))).thenReturn(equipment);
        when(equipmentHistoryRepositoryStub.save(any(EquipmentHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = sut.create(
                "Atemschutzgerät PA 300", "INV-007", null,
                EquipmentStatus.VERFUEGBAR, category.getId(), null, inspectionDate, maintenanceDate
        );

        // Assert
        assertThat(result.getNextInspectionDate()).isEqualTo(inspectionDate);
        assertThat(result.getNextMaintenanceDate()).isEqualTo(maintenanceDate);
    }

    // ─── getAll ──────────────────────────────────────────────────────────────

    @Test
    void getAll_returnsPageOfEquipment() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-010", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        Page<Equipment> page = new PageImpl<>(List.of(equipment));
        when(equipmentRepositoryStub.findAllWithFilters(
                null, null, null, null, null, PageRequest.of(0, 20)
        )).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, null, null, null, null, null);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Atemschutzgerät PA 300");
    }

    @Test
    void getAll_passesSearchParameterToRepository() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-011", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        Page<Equipment> page = new PageImpl<>(List.of(equipment));
        when(equipmentRepositoryStub.findAllWithFilters(
                "Atemschutz", null, null, null, null, PageRequest.of(0, 20)
        )).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, "Atemschutz", null, null, null, null);

        // Assert
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAll_treatsBlankSearchAsNull() {
        // Arrange
        Page<Equipment> page = new PageImpl<>(List.of());
        when(equipmentRepositoryStub.findAllWithFilters(
                null, null, null, null, null, PageRequest.of(0, 20)
        )).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, "   ", null, null, null, null);

        // Assert
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void getAll_filtersByCategoryId() {
        // Arrange
        var categoryId = UUID.randomUUID();
        Page<Equipment> page = new PageImpl<>(List.of());
        when(equipmentRepositoryStub.findAllWithFilters(
                null, categoryId, null, null, null, PageRequest.of(0, 20)
        )).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, null, categoryId, null, null, null);

        // Assert
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void getAll_filtersByDueBefore() {
        // Arrange
        var dueBefore = LocalDate.of(2025, 12, 31);
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-012", null,
                EquipmentStatus.VERFUEGBAR, category, null, LocalDate.of(2025, 6, 1), null
        );
        Page<Equipment> page = new PageImpl<>(List.of(equipment));
        when(equipmentRepositoryStub.findAllWithFilters(
                null, null, null, null, dueBefore, PageRequest.of(0, 20)
        )).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, null, null, null, null, dueBefore);

        // Assert
        assertThat(result.getContent()).hasSize(1);
    }

    // ─── getById ─────────────────────────────────────────────────────────────

    @Test
    void getById_returnsEquipmentWhenFound() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-020", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));

        // Act
        var result = sut.getById(equipment.getId());

        // Assert
        assertThat(result.getName()).isEqualTo("Atemschutzgerät PA 300");
        assertThat(result.getId()).isEqualTo(equipment.getId());
    }

    @Test
    void getById_throwsExceptionWhenNotFound() {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentRepositoryStub.findById(equipmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(equipmentId))
                .isInstanceOf(EquipmentNotFoundException.class)
                .hasMessageContaining(equipmentId.toString());
    }

    @Test
    void getById_throwsExceptionWhenArchived() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-021", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        equipment.setArchived(true);
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(equipment.getId()))
                .isInstanceOf(EquipmentNotFoundException.class)
                .hasMessageContaining(equipment.getId().toString());
    }

    @Test
    void getById_throwsExceptionWhenIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> sut.getById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("equipmentId must not be null");
    }

    // ─── update ──────────────────────────────────────────────────────────────

    @Test
    void update_returnsUpdatedEquipment() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-030", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = sut.update(
                equipment.getId(), "Atemschutzgerät PA 500", null, "Neues Modell",
                null, null, null, false, null, false, null, false
        );

        // Assert
        assertThat(result.getName()).isEqualTo("Atemschutzgerät PA 500");
        assertThat(result.getDescription()).isEqualTo("Neues Modell");
    }

    @Test
    void update_savesHistoryWhenStatusChanges() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-031", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(equipmentHistoryRepositoryStub.save(any(EquipmentHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        sut.update(
                equipment.getId(), null, null, null,
                EquipmentStatus.DEFEKT, null, null, false, null, false, null, false
        );

        // Assert
        verify(equipmentHistoryRepositoryStub).save(any(EquipmentHistory.class));
    }

    @Test
    void update_doesNotSaveHistoryWhenStatusUnchanged() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-032", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        sut.update(
                equipment.getId(), "Neuer Name", null, null,
                EquipmentStatus.VERFUEGBAR, null, null, false, null, false, null, false
        );

        // Assert
        // no history saved when status is unchanged
        verify(equipmentHistoryRepositoryStub, org.mockito.Mockito.never())
                .save(any(EquipmentHistory.class));
    }

    @Test
    void update_throwsConflictWhenInventoryNumberTaken() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-033", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.existsByInventoryNumberAndIdNotAndArchivedFalse("INV-999", equipment.getId()))
                .thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> sut.update(
                equipment.getId(), null, "INV-999", null,
                null, null, null, false, null, false, null, false
        )).isInstanceOf(EquipmentConflictException.class)
                .hasMessageContaining("INV-999");
    }

    @Test
    void update_throwsNotFoundWhenEquipmentMissing() {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentRepositoryStub.findById(equipmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.update(
                equipmentId, "Name", null, null,
                null, null, null, false, null, false, null, false
        )).isInstanceOf(EquipmentNotFoundException.class)
                .hasMessageContaining(equipmentId.toString());
    }

    @Test
    void update_removesVehicleWhenVehicleIdPresentButNull() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle(
                "LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", VehicleStatus.VERFUEGBAR, vehicleGroup
        );
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-034", null,
                EquipmentStatus.VERFUEGBAR, category, vehicle, null, null
        );
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = sut.update(
                equipment.getId(), null, null, null,
                null, null, null, true, null, false, null, false
        );

        // Assert
        assertThat(result.getVehicle()).isNull();
    }

    @Test
    void update_updatesInspectionDateWhenPresent() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-035", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        var newDate = LocalDate.of(2026, 3, 15);
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = sut.update(
                equipment.getId(), null, null, null,
                null, null, null, false, newDate, true, null, false
        );

        // Assert
        assertThat(result.getNextInspectionDate()).isEqualTo(newDate);
    }

    @Test
    void update_updatesCategoryWhenCategoryIdProvided() {
        // Arrange
        var oldCategory = new EquipmentCategory("Atemschutz", null);
        var newCategory = new EquipmentCategory("Werkzeug", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-036", null,
                EquipmentStatus.VERFUEGBAR, oldCategory, null, null, null
        );
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentCategoryServiceStub.getById(newCategory.getId())).thenReturn(newCategory);
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        var result = sut.update(
                equipment.getId(), null, null, null,
                null, newCategory.getId(), null, false, null, false, null, false
        );

        // Assert
        assertThat(result.getCategory().getId()).isEqualTo(newCategory.getId());
    }

    // ─── deleteById ──────────────────────────────────────────────────────────

    @Test
    void deleteById_archivesEquipmentAndSetsStatusToArchiviert() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-040", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentRepositoryStub.save(any(Equipment.class)))
                .thenAnswer(inv -> inv.getArgument(0));
        when(equipmentHistoryRepositoryStub.save(any(EquipmentHistory.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // Act
        sut.deleteById(equipment.getId());

        // Assert
        assertThat(equipment.isArchived()).isTrue();
        assertThat(equipment.getStatus()).isEqualTo(EquipmentStatus.ARCHIVIERT);
        verify(equipmentHistoryRepositoryStub).save(any(EquipmentHistory.class));
    }

    @Test
    void deleteById_throwsExceptionWhenNotFound() {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentRepositoryStub.findById(equipmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.deleteById(equipmentId))
                .isInstanceOf(EquipmentNotFoundException.class)
                .hasMessageContaining(equipmentId.toString());
    }

    // ─── getHistory ──────────────────────────────────────────────────────────

    @Test
    void getHistory_returnsHistoryEntriesForEquipment() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-050", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        var history1 = new EquipmentHistory(equipment, null, EquipmentStatus.VERFUEGBAR);
        var history2 = new EquipmentHistory(equipment, EquipmentStatus.VERFUEGBAR, EquipmentStatus.DEFEKT);
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentHistoryRepositoryStub.findByEquipmentIdOrderByChangedAtAsc(equipment.getId()))
                .thenReturn(List.of(history1, history2));

        // Act
        var result = sut.getHistory(equipment.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().getPreviousStatus()).isNull();
        assertThat(result.getFirst().getNewStatus()).isEqualTo(EquipmentStatus.VERFUEGBAR);
        assertThat(result.getLast().getPreviousStatus()).isEqualTo(EquipmentStatus.VERFUEGBAR);
        assertThat(result.getLast().getNewStatus()).isEqualTo(EquipmentStatus.DEFEKT);
    }

    @Test
    void getHistory_throwsExceptionWhenEquipmentNotFound() {
        // Arrange
        var equipmentId = UUID.randomUUID();
        when(equipmentRepositoryStub.findById(equipmentId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getHistory(equipmentId))
                .isInstanceOf(EquipmentNotFoundException.class)
                .hasMessageContaining(equipmentId.toString());
    }

    @Test
    void getHistory_returnsEmptyListWhenNoHistoryExists() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", "INV-051", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        when(equipmentRepositoryStub.findById(equipment.getId()))
                .thenReturn(Optional.of(equipment));
        when(equipmentHistoryRepositoryStub.findByEquipmentIdOrderByChangedAtAsc(equipment.getId()))
                .thenReturn(List.of());

        // Act
        var result = sut.getHistory(equipment.getId());

        // Assert
        assertThat(result).isEmpty();
    }
}
