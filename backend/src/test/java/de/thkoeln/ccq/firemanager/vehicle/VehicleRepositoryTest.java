package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("integration")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class VehicleRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleRepository sut;

    @Test
    void findAll_returnsPersistedEntities() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        entityManager.persistAndFlush(vehicle);
        entityManager.clear();

        // Act
        var result = sut.findAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("LF 10/6");
    }

    @Test
    void findByVehicleGroupId_returnsVehiclesWithGroup() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle1 = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        var vehicle2 = new Vehicle("LF 20/16", "Funk-02", "M-CD5678", 2018, "Desc",
                Vehicle.VehicleStatus.WARTUNG, vehicleGroup);
        entityManager.persistAndFlush(vehicle1);
        entityManager.persistAndFlush(vehicle2);
        entityManager.clear();

        // Act
        var result = sut.findByVehicleGroupId(vehicleGroup.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Vehicle::getName)
                .containsExactlyInAnyOrder("LF 10/6", "LF 20/16");
    }

    @Test
    void findByStatus_returnsVehiclesWithStatus() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle1 = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        var vehicle2 = new Vehicle("LF 20/16", "Funk-02", "M-CD5678", 2018, "Desc",
                Vehicle.VehicleStatus.WARTUNG, vehicleGroup);
        entityManager.persistAndFlush(vehicle1);
        entityManager.persistAndFlush(vehicle2);
        entityManager.clear();

        // Act
        var result = sut.findByStatus(Vehicle.VehicleStatus.VERFUEGBAR);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("LF 10/6");
    }

    @Test
    void findByVehicleGroupIdAndStatus_returnsVehiclesWithGroupAndStatus() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle1 = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        var vehicle2 = new Vehicle("LF 20/16", "Funk-02", "M-CD5678", 2018, "Desc",
                Vehicle.VehicleStatus.WARTUNG, vehicleGroup);
        var vehicle3 = new Vehicle("LF 20/16 (2)", "Funk-03", "M-EF9012", 2022, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        entityManager.persistAndFlush(vehicle1);
        entityManager.persistAndFlush(vehicle2);
        entityManager.persistAndFlush(vehicle3);
        entityManager.clear();

        // Act
        var result = sut.findByVehicleGroupIdAndStatus(vehicleGroup.getId(), Vehicle.VehicleStatus.VERFUEGBAR);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(Vehicle::getName)
                .containsExactlyInAnyOrder("LF 10/6", "LF 20/16 (2)");
    }

    @Test
    void save_createsNewEntity() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);

        // Act
        var saved = sut.save(vehicle);

        // Assert
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("LF 10/6");
    }

    @Test
    void deleteById_removesEntity() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);
        entityManager.persistAndFlush(vehicle);
        var vehicleId = vehicle.getId();
        entityManager.clear();

        // Act
        sut.deleteById(vehicleId);

        // Assert
        assertThat(sut.findById(vehicleId)).isEmpty();
    }

    @Test
    void save_withNullName_throwsException() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle = new Vehicle(null, "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, vehicleGroup);

        // Act & Assert
        assertThatThrownBy(() -> sut.save(vehicle))
                .isInstanceOf(Exception.class);
    }

    @Test
    void save_withNullVehicleGroup_throwsException() {
        // Arrange
        var vehicle = new Vehicle("LF 10/6", "Funk-01", "M-AB1234", 2020, "Desc",
                Vehicle.VehicleStatus.VERFUEGBAR, null);

        // Act & Assert
        assertThatThrownBy(() -> sut.save(vehicle))
                .isInstanceOf(Exception.class);
    }
}