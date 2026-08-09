package de.thkoeln.ccq.firemanager.location;

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
class LocationRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private LocationRepository sut;

    @Test
    void findAll_returnsPersistedLocations() {
        // Arrange
        Location location1 = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        Location location2 = new Location("Gerätehaus Bonn", "Beispielweg 2, 53111 Bonn", "GERAETEHAUS");
        
        entityManager.persistAndFlush(location1);
        entityManager.persistAndFlush(location2);
        entityManager.clear();

        // Act
        List<Location> result = sut.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(location1, location2);
    }

    @Test
    void findById_returnsLocationWhenExists() {
        // Arrange
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        entityManager.persistAndFlush(location);
        entityManager.clear();

        // Act
        var result = sut.findById(location.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(location);
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
    void save_persistsLocation() {
        // Arrange
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");

        // Act
        Location savedLocation = sut.save(location);
        entityManager.clear();

        var foundLocation = sut.findById(savedLocation.getId());

        // Assert
        assertThat(foundLocation).isPresent();
        assertThat(foundLocation.get()).isEqualTo(location);
    }

    @Test
    void deleteById_removesLocation() {
        // Arrange
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        entityManager.persistAndFlush(location);
        entityManager.clear();

        // Act
        sut.deleteById(location.getId());

        var result = sut.findById(location.getId());

        // Assert
        assertThat(result).isEmpty();
    }
}