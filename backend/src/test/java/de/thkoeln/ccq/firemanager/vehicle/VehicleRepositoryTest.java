package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.domain.PageRequest;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

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
    void findByArchivedFalse_returnsOnlyNonArchivedVehicles() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var activeVehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", VehicleStatus.VERFUEGBAR, vehicleGroup);
        var archivedVehicle = new Vehicle("TLF 16/25", "Florian Köln 1-21-1", "K-CD 5678",
                2018, "Tanklöschfahrzeug", VehicleStatus.VERFUEGBAR, vehicleGroup);
        archivedVehicle.setArchived(true);

        entityManager.persistAndFlush(activeVehicle);
        entityManager.persistAndFlush(archivedVehicle);
        entityManager.clear();

        // Act
        var result = sut.findByArchivedFalse(PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(activeVehicle.getId());
    }

    @Test
    void findByVehicleGroupIdAndArchivedFalse_returnsVehiclesInGroup() {
        // Arrange
        var group1 = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var group2 = new VehicleGroup("Rettungsdienst", "Beschreibung");
        entityManager.persistAndFlush(group1);
        entityManager.persistAndFlush(group2);

        var vehicle1 = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, group1);
        var vehicle2 = new Vehicle("RTW 1", "Florian Köln 1-83-1", "K-EF 9012",
                2021, "Beschreibung", VehicleStatus.VERFUEGBAR, group2);
        entityManager.persistAndFlush(vehicle1);
        entityManager.persistAndFlush(vehicle2);
        entityManager.clear();

        // Act
        var result = sut.findByVehicleGroupIdAndArchivedFalse(group1.getId(), PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(vehicle1.getId());
    }

    @Test
    void findByStatusAndArchivedFalse_returnsVehiclesWithStatus() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var verfuegbar = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        var imEinsatz = new Vehicle("TLF 16/25", "Florian Köln 1-21-1", "K-CD 5678",
                2018, "Beschreibung", VehicleStatus.IM_EINSATZ, vehicleGroup);
        entityManager.persistAndFlush(verfuegbar);
        entityManager.persistAndFlush(imEinsatz);
        entityManager.clear();

        // Act
        var result = sut.findByStatusAndArchivedFalse(VehicleStatus.IM_EINSATZ, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(imEinsatz.getId());
    }

    @Test
    void findByVehicleGroupIdAndStatusAndArchivedFalse_filtersCorrectly() {
        // Arrange
        var group1 = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var group2 = new VehicleGroup("Rettungsdienst", "Beschreibung");
        entityManager.persistAndFlush(group1);
        entityManager.persistAndFlush(group2);

        var vehicle1 = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, group1);
        var vehicle2 = new Vehicle("TLF 16/25", "Florian Köln 1-21-1", "K-CD 5678",
                2018, "Beschreibung", VehicleStatus.IM_EINSATZ, group1);
        var vehicle3 = new Vehicle("RTW 1", "Florian Köln 1-83-1", "K-EF 9012",
                2021, "Beschreibung", VehicleStatus.VERFUEGBAR, group2);
        entityManager.persistAndFlush(vehicle1);
        entityManager.persistAndFlush(vehicle2);
        entityManager.persistAndFlush(vehicle3);
        entityManager.clear();

        // Act
        var result = sut.findByVehicleGroupIdAndStatusAndArchivedFalse(
                group1.getId(), VehicleStatus.VERFUEGBAR, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(vehicle1.getId());
    }

    @Test
    void existsByVehicleGroupIdAndArchivedFalse_returnsTrueWhenExists() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        entityManager.persistAndFlush(vehicle);
        entityManager.clear();

        // Act
        var result = sut.existsByVehicleGroupIdAndArchivedFalse(vehicleGroup.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void existsByVehicleGroupIdAndArchivedFalse_returnsFalseWhenAllArchived() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        vehicle.setArchived(true);
        entityManager.persistAndFlush(vehicle);
        entityManager.clear();

        // Act
        var result = sut.existsByVehicleGroupIdAndArchivedFalse(vehicleGroup.getId());

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void save_persistsVehicleWithAllFields() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);

        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", VehicleStatus.VERFUEGBAR, vehicleGroup);

        // Act
        var savedVehicle = sut.save(vehicle);
        entityManager.flush();
        entityManager.clear();
        var foundVehicle = sut.findById(savedVehicle.getId());

        // Assert
        assertThat(foundVehicle).isPresent();
        assertThat(foundVehicle.get().getName()).isEqualTo("LF 10/6");
        assertThat(foundVehicle.get().getFunkrufname()).isEqualTo("Florian Köln 1-46-1");
        assertThat(foundVehicle.get().getKennzeichen()).isEqualTo("K-AB 1234");
        assertThat(foundVehicle.get().getBaujahr()).isEqualTo(2020);
        assertThat(foundVehicle.get().getBeschreibung()).isEqualTo("Löschgruppenfahrzeug");
        assertThat(foundVehicle.get().getStatus()).isEqualTo(VehicleStatus.VERFUEGBAR);
        assertThat(foundVehicle.get().getVehicleGroup().getId()).isEqualTo(vehicleGroup.getId());
        assertThat(foundVehicle.get().isArchived()).isFalse();
    }
}
