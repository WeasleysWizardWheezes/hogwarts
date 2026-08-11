package de.thkoeln.ccq.firemanager.vehiclegroup;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
    void create_returnsVehicleGroupWhenNameIsUnique() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupRepositoryStub.existsByName("Löschfahrzeuge")).thenReturn(false);
        when(vehicleGroupRepositoryStub.save(any(VehicleGroup.class))).thenReturn(vehicleGroup);

        // Act
        var result = sut.create("Löschfahrzeuge", "Beschreibung");

        // Assert
        assertThat(result.getName()).isEqualTo("Löschfahrzeuge");
        assertThat(result.getDescription()).isEqualTo("Beschreibung");
        assertThat(result.getId()).isNotNull();
    }

    @Test
    void create_throwsConflictExceptionWhenNameExists() {
        // Arrange
        when(vehicleGroupRepositoryStub.existsByName("Löschfahrzeuge")).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> sut.create("Löschfahrzeuge", "Beschreibung"))
                .isInstanceOf(de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupConflictException.class)
                .hasMessageContaining("already exists");
    }

    @Test
    void getAll_returnsAllVehicleGroups() {
        // Arrange
        var group1 = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var group2 = new VehicleGroup("Rettungsdienst", "Beschreibung");
        when(vehicleGroupRepositoryStub.findAll()).thenReturn(List.of(group1, group2));

        // Act
        var result = sut.getAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(VehicleGroup::getName).containsExactlyInAnyOrder("Löschfahrzeuge", "Rettungsdienst");
    }

    @Test
    void getById_returnsVehicleGroupWhenIdExists() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupRepositoryStub.findById(vehicleGroup.getId())).thenReturn(Optional.of(vehicleGroup));

        // Act
        var result = sut.getById(vehicleGroup.getId());

        // Assert
        assertThat(result.getName()).isEqualTo("Löschfahrzeuge");
    }

    @Test
    void getById_throwsWhenNotFound() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupRepositoryStub.findById(vehicleGroupId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(vehicleGroupId))
                .isInstanceOf(de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException.class)
                .hasMessageContaining(vehicleGroupId.toString());
    }

    @Test
    void update_returnsUpdatedVehicleGroup() {
        // Arrange
        var existing = new VehicleGroup("Löschfahrzeuge", "Alte Beschreibung");
        var vehicleGroupId = existing.getId();
        when(vehicleGroupRepositoryStub.findById(vehicleGroupId)).thenReturn(Optional.of(existing));
        when(vehicleGroupRepositoryStub.save(existing)).thenReturn(existing);

        // Act
        var result = sut.update(vehicleGroupId, "Löschfahrzeuge (aktualisiert)", "Neue Beschreibung");

        // Assert
        assertThat(result.getName()).isEqualTo("Löschfahrzeuge (aktualisiert)");
        assertThat(result.getDescription()).isEqualTo("Neue Beschreibung");
    }

    @Test
    void update_throwsWhenNotFound() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupRepositoryStub.findById(vehicleGroupId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.update(vehicleGroupId, "Neuer Name", "Neue Beschreibung"))
                .isInstanceOf(de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException.class)
                .hasMessageContaining(vehicleGroupId.toString());
    }

    @Test
    void deleteById_successWhenIdExists() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupRepositoryStub.existsById(vehicleGroupId)).thenReturn(true);

        // Act
        sut.deleteById(vehicleGroupId);

        // Assert
        verify(vehicleGroupRepositoryStub).deleteById(vehicleGroupId);
    }

    @Test
    void deleteById_throwsWhenNotFound() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleGroupRepositoryStub.existsById(vehicleGroupId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> sut.deleteById(vehicleGroupId))
                .isInstanceOf(de.thkoeln.ccq.firemanager.vehiclegroup.exception.VehicleGroupNotFoundException.class)
                .hasMessageContaining(vehicleGroupId.toString());
    }
}