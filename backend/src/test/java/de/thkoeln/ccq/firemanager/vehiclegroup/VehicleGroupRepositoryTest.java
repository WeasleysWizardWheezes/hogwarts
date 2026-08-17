package de.thkoeln.ccq.firemanager.vehiclegroup;

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
class VehicleGroupRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private VehicleGroupRepository sut;

    @Test
    void findByArchivedFalse_returnsOnlyNonArchivedGroups() {
        // Arrange
        var activeGroup = new VehicleGroup("Löschfahrzeuge", "Aktive Gruppe");
        var archivedGroup = new VehicleGroup("Rettungsdienst", "Archivierte Gruppe");
        archivedGroup.setArchived(true);

        entityManager.persistAndFlush(activeGroup);
        entityManager.persistAndFlush(archivedGroup);
        entityManager.clear();

        // Act
        var result = sut.findByArchivedFalse(PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().getId()).isEqualTo(activeGroup.getId());
    }

    @Test
    void findByArchivedFalse_returnsEmptyPageWhenAllArchived() {
        // Arrange
        var archivedGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        archivedGroup.setArchived(true);
        entityManager.persistAndFlush(archivedGroup);
        entityManager.clear();

        // Act
        var result = sut.findByArchivedFalse(PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    void findAll_returnsAllPersistedGroups() {
        // Arrange
        var group1 = new VehicleGroup("Löschfahrzeuge", "Beschreibung 1");
        var group2 = new VehicleGroup("Rettungsdienst", "Beschreibung 2");
        entityManager.persistAndFlush(group1);
        entityManager.persistAndFlush(group2);
        entityManager.clear();

        // Act
        var result = sut.findAll();

        // Assert
        assertThat(result)
                .extracting(VehicleGroup::getId)
                .containsExactlyInAnyOrder(group1.getId(), group2.getId());
    }

    @Test
    void findById_returnsVehicleGroupWhenExists() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);
        entityManager.clear();

        // Act
        var result = sut.findById(vehicleGroup.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Löschfahrzeuge");
        assertThat(result.get().getBeschreibung()).isEqualTo("Beschreibung");
    }

    @Test
    void findById_returnsEmptyWhenNotExists() {
        // Arrange
        var nonExistentId = java.util.UUID.randomUUID();

        // Act
        var result = sut.findById(nonExistentId);

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void save_persistsVehicleGroup() {
        // Arrange
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");

        // Act
        var savedGroup = sut.save(vehicleGroup);
        entityManager.flush();
        entityManager.clear();
        var foundGroup = sut.findById(savedGroup.getId());

        // Assert
        assertThat(foundGroup).isPresent();
        assertThat(foundGroup.get().getName()).isEqualTo("Löschfahrzeuge");
        assertThat(foundGroup.get().getBeschreibung()).isEqualTo("Beschreibung");
        assertThat(foundGroup.get().isArchived()).isFalse();
        assertThat(foundGroup.get().getCreatedAt()).isNotNull();
        assertThat(foundGroup.get().getUpdatedAt()).isNotNull();
    }
}
