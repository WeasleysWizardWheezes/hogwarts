package de.thkoeln.ccq.firemanager.equipmentcategory;

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
class EquipmentCategoryRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EquipmentCategoryRepository sut;

    // ─── findAllWithSearch ───────────────────────────────────────────────────

    @Test
    void findAllWithSearch_returnsAllNonArchivedCategories() {
        // Arrange
        var cat1 = new EquipmentCategory("Atemschutz", "Atemschutzgeräte");
        var cat2 = new EquipmentCategory("Funk", "Funkgeräte");
        entityManager.persistAndFlush(cat1);
        entityManager.persistAndFlush(cat2);
        entityManager.clear();

        // Act
        var result = sut.findAllWithSearch(null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
    }

    @Test
    void findAllWithSearch_excludesArchivedCategories() {
        // Arrange
        var cat1 = new EquipmentCategory("Atemschutz", null);
        var cat2 = new EquipmentCategory("Funk", null);
        cat2.setArchived(true);
        entityManager.persistAndFlush(cat1);
        entityManager.persistAndFlush(cat2);
        entityManager.clear();

        // Act
        var result = sut.findAllWithSearch(null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Atemschutz");
    }

    @Test
    void findAllWithSearch_filtersByName() {
        // Arrange
        var cat1 = new EquipmentCategory("Atemschutz", null);
        var cat2 = new EquipmentCategory("Funk", null);
        entityManager.persistAndFlush(cat1);
        entityManager.persistAndFlush(cat2);
        entityManager.clear();

        // Act
        var result = sut.findAllWithSearch("Atem", PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Atemschutz");
    }

    @Test
    void findAllWithSearch_isCaseInsensitive() {
        // Arrange
        var cat = new EquipmentCategory("Atemschutz", null);
        entityManager.persistAndFlush(cat);
        entityManager.clear();

        // Act
        var result = sut.findAllWithSearch("atemschutz", PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    // ─── existsByNameAndArchivedFalse ────────────────────────────────────────

    @Test
    void existsByNameAndArchivedFalse_returnsTrueWhenExists() {
        // Arrange
        var cat = new EquipmentCategory("Atemschutz", null);
        entityManager.persistAndFlush(cat);
        entityManager.clear();

        // Act & Assert
        assertThat(sut.existsByNameAndArchivedFalse("Atemschutz")).isTrue();
    }

    @Test
    void existsByNameAndArchivedFalse_returnsFalseWhenArchived() {
        // Arrange
        var cat = new EquipmentCategory("Atemschutz", null);
        cat.setArchived(true);
        entityManager.persistAndFlush(cat);
        entityManager.clear();

        // Act & Assert
        assertThat(sut.existsByNameAndArchivedFalse("Atemschutz")).isFalse();
    }

    // ─── existsByNameAndIdNotAndArchivedFalse ────────────────────────────────

    @Test
    void existsByNameAndIdNotAndArchivedFalse_returnsTrueForDifferentId() {
        // Arrange
        var cat1 = new EquipmentCategory("Atemschutz", null);
        var cat2 = new EquipmentCategory("Funk", null);
        entityManager.persistAndFlush(cat1);
        entityManager.persistAndFlush(cat2);
        entityManager.clear();

        // Act & Assert
        assertThat(sut.existsByNameAndIdNotAndArchivedFalse("Atemschutz", cat2.getId())).isTrue();
    }

    @Test
    void existsByNameAndIdNotAndArchivedFalse_returnsFalseForSameId() {
        // Arrange
        var cat = new EquipmentCategory("Atemschutz", null);
        entityManager.persistAndFlush(cat);
        entityManager.clear();

        // Act & Assert
        assertThat(sut.existsByNameAndIdNotAndArchivedFalse("Atemschutz", cat.getId())).isFalse();
    }
}
