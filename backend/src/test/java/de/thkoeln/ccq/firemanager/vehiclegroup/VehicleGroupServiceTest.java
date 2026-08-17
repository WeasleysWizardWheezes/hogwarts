package de.thkoeln.ccq.firemanager.vehiclegroup;

import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupConflictException;
import de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException;
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
class VehicleGroupServiceTest {

    @Mock
    private VehicleGroupRepository vehicleGroupRepositoryStub;

    @InjectMocks
    private VehicleGroupService sut;

    @Test
    void create_returnsVehicleGroupWithGeneratedId() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Alle Löschfahrzeuge");
        when(vehicleGroupRepositoryStub.save(any(VehicleGroup.class))).thenReturn(vehicleGroup);

        // Act
        var result = sut.create("Löschfahrzeuge", "Alle Löschfahrzeuge");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("Löschfahrzeuge");
        assertThat(result.getBeschreibung()).isEqualTo("Alle Löschfahrzeuge");
    }

    @Test
    void create_withNameOnly_returnsVehicleGroup() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Rettungsdienst");
        when(vehicleGroupRepositoryStub.save(any(VehicleGroup.class))).thenReturn(vehicleGroup);

        // Act
        var result = sut.create("Rettungsdienst");

        // Assert
        assertThat(result.getName()).isEqualTo("Rettungsdienst");
        assertThat(result.getBeschreibung()).isNull();
    }

    @Test
    void getAll_returnsPageOfVehicleGroups() {
        // Arrange
        var vehicleGroup1 = new VehicleGroup("Löschfahrzeuge", "Beschreibung 1");
        var vehicleGroup2 = new VehicleGroup("Rettungsdienst", "Beschreibung 2");
        Page<VehicleGroup> page = new PageImpl<>(List.of(vehicleGroup1, vehicleGroup2));
        when(vehicleGroupRepositoryStub.findByArchivedFalse(PageRequest.of(0, 20))).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20);

        // Assert
        assertThat(result.getContent()).hasSize(2);
    }

    @Test
    void getById_returnsVehicleGroupWhenIdExists() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupRepositoryStub.findById(vehicleGroup.getId()))
                .thenReturn(Optional.of(vehicleGroup));

        // Act
        var result = sut.getById(vehicleGroup.getId());

        // Assert
        assertThat(result.getName()).isEqualTo("Löschfahrzeuge");
    }

    @Test
    void getById_throwsExceptionWhenNotFound() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupRepositoryStub.findById(vehicleGroupId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(vehicleGroupId))
                .isInstanceOf(VehicleGroupNotFoundException.class)
                .hasMessageContaining(vehicleGroupId.toString());
    }

    @Test
    void getById_throwsExceptionWhenIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> sut.getById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("vehicleGroupId must not be null");
    }

    @Test
    void getById_throwsExceptionWhenArchived() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        vehicleGroup.setArchived(true);
        when(vehicleGroupRepositoryStub.findById(vehicleGroup.getId()))
                .thenReturn(Optional.of(vehicleGroup));

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(vehicleGroup.getId()))
                .isInstanceOf(VehicleGroupNotFoundException.class)
                .hasMessageContaining(vehicleGroup.getId().toString());
    }

    @Test
    void deleteById_archivesVehicleGroup() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupRepositoryStub.findById(vehicleGroup.getId()))
                .thenReturn(Optional.of(vehicleGroup));
        when(vehicleGroupRepositoryStub.save(any(VehicleGroup.class))).thenReturn(vehicleGroup);

        // Act
        sut.deleteById(vehicleGroup.getId());

        // Assert
        verify(vehicleGroupRepositoryStub).save(any(VehicleGroup.class));
        assertThat(vehicleGroup.isArchived()).isTrue();
    }

    @Test
    void deleteById_throwsExceptionWhenNotFound() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupRepositoryStub.findById(vehicleGroupId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.deleteById(vehicleGroupId))
                .isInstanceOf(VehicleGroupNotFoundException.class)
                .hasMessageContaining(vehicleGroupId.toString());
    }

    @Test
    void deleteByIdWithConflictCheck_throwsConflictWhenHasNonArchivedVehicles() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> sut.deleteByIdWithConflictCheck(vehicleGroupId, true))
                .isInstanceOf(VehicleGroupConflictException.class)
                .hasMessageContaining(vehicleGroupId.toString())
                .hasMessageContaining("non-archived vehicles");
    }

    @Test
    void deleteByIdWithConflictCheck_archivesWhenNoNonArchivedVehicles() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupRepositoryStub.findById(vehicleGroup.getId()))
                .thenReturn(Optional.of(vehicleGroup));
        when(vehicleGroupRepositoryStub.save(any(VehicleGroup.class))).thenReturn(vehicleGroup);

        // Act
        sut.deleteByIdWithConflictCheck(vehicleGroup.getId(), false);

        // Assert
        assertThat(vehicleGroup.isArchived()).isTrue();
    }

    @Test
    void update_returnsUpdatedVehicleGroup() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Alte Beschreibung");
        when(vehicleGroupRepositoryStub.findById(vehicleGroup.getId()))
                .thenReturn(Optional.of(vehicleGroup));
        when(vehicleGroupRepositoryStub.save(any(VehicleGroup.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = sut.update(vehicleGroup.getId(), "Löschfahrzeuge (aktualisiert)", "Neue Beschreibung");

        // Assert
        assertThat(result.getName()).isEqualTo("Löschfahrzeuge (aktualisiert)");
        assertThat(result.getBeschreibung()).isEqualTo("Neue Beschreibung");
    }

    @Test
    void update_throwsExceptionWhenNotFound() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupRepositoryStub.findById(vehicleGroupId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.update(vehicleGroupId, "Name", "Beschreibung"))
                .isInstanceOf(VehicleGroupNotFoundException.class)
                .hasMessageContaining(vehicleGroupId.toString());
    }
}
