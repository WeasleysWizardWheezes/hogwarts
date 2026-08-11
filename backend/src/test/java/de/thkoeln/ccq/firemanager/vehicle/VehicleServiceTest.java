package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroupService;
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
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepositoryStub;

    @Mock
    private VehicleGroupService vehicleGroupServiceStub;

    @InjectMocks
    private VehicleService sut;

    @Test
    void create_returnsVehicleWhenGroupExists() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleGroupServiceStub.getById(vehicleGroup.getId())).thenReturn(vehicleGroup);
        when(vehicleRepositoryStub.save(any(Vehicle.class))).thenReturn(vehicle);

        // Act
        var result = sut.create("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup.getId());

        // Assert
        assertThat(result.getName()).isEqualTo("LF 10/6");
        assertThat(result.getRadioCallName()).isEqualTo("Funk-01");
        assertThat(result.getLicensePlate()).isEqualTo("M-AB1234");
        assertThat(result.getStatus()).isEqualTo(Vehicle.VehicleStatus.VERFUEGBAR);
    }

    @Test
    void getAll_returnsAllVehicles() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle1 = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        var vehicle2 = new Vehicle("LF 20/16", "Funk-02", "M-CD5678", 2018, "Desc",
                Vehicle.VehicleStatus.WARTUNG, vehicleGroup);
        when(vehicleRepositoryStub.findAll()).thenReturn(List.of(vehicle1, vehicle2));

        // Act
        var result = sut.getAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Vehicle::getName).containsExactlyInAnyOrder("LF 10/6", "LF 20/16");
    }

    @Test
    void getById_returnsVehicleWhenIdExists() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        when(vehicleRepositoryStub.findById(vehicle.getId())).thenReturn(Optional.of(vehicle));

        // Act
        var result = sut.getById(vehicle.getId());

        // Assert
        assertThat(result.getName()).isEqualTo("LF 10/6");
    }

    @Test
    void getById_throwsWhenNotFound() {
        // Arrange
        var vehicleId = UUID.randomUUID();
        when(vehicleRepositoryStub.findById(vehicleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(vehicleId))
                .isInstanceOf(de.thkoeln.ccq.firemanager.vehicle.exception.VehicleNotFoundException.class)
                .hasMessageContaining(vehicleId.toString());
    }

    @Test
    void update_returnsUpdatedVehicle() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var existing = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        var vehicleId = existing.getId();
        when(vehicleRepositoryStub.findById(vehicleId)).thenReturn(Optional.of(existing));
        when(vehicleGroupServiceStub.getById(vehicleGroup.getId())).thenReturn(vehicleGroup);
        when(vehicleRepositoryStub.save(existing)).thenReturn(existing);

        // Act
        var result = sut.update(vehicleId, "LF 10/6 (aktualisiert)", "Funk-01-updated", "M-AB1234", 2020, "Neue Desc",
                Vehicle.VehicleStatus.WARTUNG, vehicleGroup.getId());

        // Assert
        assertThat(result.getName()).isEqualTo("LF 10/6 (aktualisiert)");
        assertThat(result.getDescription()).isEqualTo("Neue Desc");
    }

    @Test
    void deleteById_successWhenVehicleNotInUse() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        var vehicleId = vehicle.getId();
        when(vehicleRepositoryStub.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        // Act
        sut.deleteById(vehicleId);

        // Assert
        verify(vehicleRepositoryStub).deleteById(vehicleId);
    }

    @Test
    void deleteById_throwsInUseExceptionWhenStatusIsInUse() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.IM_EINSATZ, vehicleGroup);
        var vehicleId = vehicle.getId();
        when(vehicleRepositoryStub.findById(vehicleId)).thenReturn(Optional.of(vehicle));

        // Act & Assert
        assertThatThrownBy(() -> sut.deleteById(vehicleId))
                .isInstanceOf(de.thkoeln.ccq.firemanager.vehicle.exception.VehicleInUseException.class)
                .hasMessageContaining("cannot be archived while in use");
    }
}