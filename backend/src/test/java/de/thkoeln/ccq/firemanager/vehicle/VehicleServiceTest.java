package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroupService;
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
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepositoryStub;

    @Mock
    private VehicleGroupService vehicleGroupServiceStub;

    @InjectMocks
    private VehicleService sut;

    @Test
    void create_returnsVehicleWithGeneratedId() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupServiceStub.getById(vehicleGroup.getId())).thenReturn(vehicleGroup);

        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleRepositoryStub.save(any(Vehicle.class))).thenReturn(vehicle);

        // Act
        var result = sut.create("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", VehicleStatus.VERFUEGBAR, vehicleGroup.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo("LF 10/6");
        assertThat(result.getFunkrufname()).isEqualTo("Florian Köln 1-46-1");
        assertThat(result.getKennzeichen()).isEqualTo("K-AB 1234");
        assertThat(result.getStatus()).isEqualTo(VehicleStatus.VERFUEGBAR);
    }

    @Test
    void create_defaultsToVerfuegbarWhenStatusIsNull() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        when(vehicleGroupServiceStub.getById(vehicleGroup.getId())).thenReturn(vehicleGroup);

        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleRepositoryStub.save(any(Vehicle.class))).thenReturn(vehicle);

        // Act
        var result = sut.create("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", null, vehicleGroup.getId());

        // Assert
        assertThat(result.getStatus()).isEqualTo(VehicleStatus.VERFUEGBAR);
    }

    @Test
    void getAll_returnsPageOfVehicles() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));
        when(vehicleRepositoryStub.findByArchivedFalse(PageRequest.of(0, 20))).thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, null, null);

        // Assert
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAll_filtersById_vehicleGroupId() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));
        when(vehicleRepositoryStub.findByVehicleGroupIdAndArchivedFalse(vehicleGroupId, PageRequest.of(0, 20)))
                .thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, vehicleGroupId, null);

        // Assert
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAll_filtersByStatus() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.IM_EINSATZ, vehicleGroup);
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));
        when(vehicleRepositoryStub.findByStatusAndArchivedFalse(VehicleStatus.IM_EINSATZ, PageRequest.of(0, 20)))
                .thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, null, VehicleStatus.IM_EINSATZ);

        // Assert
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getAll_filtersByVehicleGroupIdAndStatus() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        Page<Vehicle> page = new PageImpl<>(List.of(vehicle));
        when(vehicleRepositoryStub.findByVehicleGroupIdAndStatusAndArchivedFalse(
                vehicleGroupId, VehicleStatus.VERFUEGBAR, PageRequest.of(0, 20)))
                .thenReturn(page);

        // Act
        var result = sut.getAll(0, 20, vehicleGroupId, VehicleStatus.VERFUEGBAR);

        // Assert
        assertThat(result.getContent()).hasSize(1);
    }

    @Test
    void getById_returnsVehicleWhenIdExists() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleRepositoryStub.findById(vehicle.getId()))
                .thenReturn(Optional.of(vehicle));

        // Act
        var result = sut.getById(vehicle.getId());

        // Assert
        assertThat(result.getName()).isEqualTo("LF 10/6");
    }

    @Test
    void getById_throwsExceptionWhenNotFound() {
        // Arrange
        var vehicleId = UUID.randomUUID();
        when(vehicleRepositoryStub.findById(vehicleId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(vehicleId))
                .isInstanceOf(VehicleNotFoundException.class)
                .hasMessageContaining(vehicleId.toString());
    }

    @Test
    void getById_throwsExceptionWhenIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> sut.getById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("vehicleId must not be null");
    }

    @Test
    void getById_throwsExceptionWhenArchived() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        vehicle.setArchived(true);
        when(vehicleRepositoryStub.findById(vehicle.getId()))
                .thenReturn(Optional.of(vehicle));

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(vehicle.getId()))
                .isInstanceOf(VehicleNotFoundException.class)
                .hasMessageContaining(vehicle.getId().toString());
    }

    @Test
    void deleteById_archivesVehicle() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleRepositoryStub.findById(vehicle.getId()))
                .thenReturn(Optional.of(vehicle));
        when(vehicleRepositoryStub.save(any(Vehicle.class))).thenReturn(vehicle);

        // Act
        sut.deleteById(vehicle.getId());

        // Assert
        verify(vehicleRepositoryStub).save(any(Vehicle.class));
        assertThat(vehicle.isArchived()).isTrue();
    }

    @Test
    void deleteById_throwsExceptionWhenNotFound() {
        // Arrange
        var vehicleId = UUID.randomUUID();
        when(vehicleRepositoryStub.findById(vehicleId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.deleteById(vehicleId))
                .isInstanceOf(VehicleNotFoundException.class)
                .hasMessageContaining(vehicleId.toString());
    }

    @Test
    void hasNonArchivedVehiclesInGroup_returnsTrueWhenExists() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleRepositoryStub.existsByVehicleGroupIdAndArchivedFalse(vehicleGroupId))
                .thenReturn(true);

        // Act
        var result = sut.hasNonArchivedVehiclesInGroup(vehicleGroupId);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void hasNonArchivedVehiclesInGroup_returnsFalseWhenNoneExist() {
        // Arrange
        var vehicleGroupId = UUID.randomUUID();
        when(vehicleRepositoryStub.existsByVehicleGroupIdAndArchivedFalse(vehicleGroupId))
                .thenReturn(false);

        // Act
        var result = sut.hasNonArchivedVehiclesInGroup(vehicleGroupId);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void update_returnsUpdatedVehicle() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleRepositoryStub.findById(vehicle.getId()))
                .thenReturn(Optional.of(vehicle));
        when(vehicleRepositoryStub.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = sut.update(vehicle.getId(), "LF 20/16", "Florian Köln 1-46-2",
                "K-CD 5678", 2022, "Neues Fahrzeug", VehicleStatus.WARTUNG, null);

        // Assert
        assertThat(result.getName()).isEqualTo("LF 20/16");
        assertThat(result.getFunkrufname()).isEqualTo("Florian Köln 1-46-2");
        assertThat(result.getKennzeichen()).isEqualTo("K-CD 5678");
        assertThat(result.getBaujahr()).isEqualTo(2022);
        assertThat(result.getStatus()).isEqualTo(VehicleStatus.WARTUNG);
    }

    @Test
    void update_changesVehicleGroup() {
        // Arrange
        var oldGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var newGroup = new VehicleGroup("Rettungsdienst", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, oldGroup);
        when(vehicleRepositoryStub.findById(vehicle.getId()))
                .thenReturn(Optional.of(vehicle));
        when(vehicleGroupServiceStub.getById(newGroup.getId())).thenReturn(newGroup);
        when(vehicleRepositoryStub.save(any(Vehicle.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = sut.update(vehicle.getId(), null, null, null, null, null, null, newGroup.getId());

        // Assert
        assertThat(result.getVehicleGroup().getId()).isEqualTo(newGroup.getId());
    }

    @Test
    void update_throwsExceptionWhenNotFound() {
        // Arrange
        var vehicleId = UUID.randomUUID();
        when(vehicleRepositoryStub.findById(vehicleId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.update(vehicleId, "Name", "Funk", "Kennzeichen",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, null))
                .isInstanceOf(VehicleNotFoundException.class)
                .hasMessageContaining(vehicleId.toString());
    }
}
