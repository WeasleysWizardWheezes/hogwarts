package de.thkoeln.ccq.firemanager.vehiclegroup;

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
    void findAll_returnsPersistedEntities() {
        // Arrange
        var group1 = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        var group2 = new VehicleGroup("Rettungsdienst", "Beschreibung");
        entityManager.persistAndFlush(group1);
        entityManager.persistAndFlush(group2);
        entityManager.clear();

        // Act
        var result = sut.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(VehicleGroup::getId)
                .containsExactlyInAnyOrder(group1.getId(), group2.getId());
    }

    @Test
    void existsByName_returnsTrueWhenNameExists() {
        // Arrange
        var group = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(group);
        entityManager.clear();

        // Act
        var exists = sut.existsByName("Löschfahrzeuge");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void existsByName_returnsFalseWhenNameDoesNotExist() {
        // Act
        var exists = sut.existsByName("Nicht vorhanden");

        // Assert
        assertThat(exists).isFalse();
    }

    @Test
    void findById_returnsEntityWhenIdExists() {
        // Arrange
        var group = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(group);
        entityManager.clear();

        // Act
        var result = sut.findById(group.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Löschfahrzeuge");
    }
}