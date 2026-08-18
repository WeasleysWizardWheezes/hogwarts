package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
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

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EquipmentHistoryRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EquipmentHistoryRepository sut;

    private Equipment persistEquipment(String inventoryNumber) {
        var category = new EquipmentCategory("Atemschutz", null);
        entityManager.persistAndFlush(category);
        var equipment = new Equipment(
                "Atemschutzgerät PA 300", inventoryNumber, null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null
        );
        return entityManager.persistAndFlush(equipment);
    }

    @Test
    void findByEquipmentIdOrderByChangedAtAsc_returnsHistoryInChronologicalOrder() {
        // Arrange
        var equipment = persistEquipment("INV-H-001");
        var h1 = new EquipmentHistory(equipment, null, EquipmentStatus.VERFUEGBAR);
        var h2 = new EquipmentHistory(equipment, EquipmentStatus.VERFUEGBAR, EquipmentStatus.IN_GEBRAUCH);
        var h3 = new EquipmentHistory(equipment, EquipmentStatus.IN_GEBRAUCH, EquipmentStatus.DEFEKT);
        entityManager.persistAndFlush(h1);
        entityManager.persistAndFlush(h2);
        entityManager.persistAndFlush(h3);
        entityManager.clear();

        // Act
        var result = sut.findByEquipmentIdOrderByChangedAtAsc(equipment.getId());

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result)
                .extracting(EquipmentHistory::getNewStatus)
                .containsExactly(
                        EquipmentStatus.VERFUEGBAR,
                        EquipmentStatus.IN_GEBRAUCH,
                        EquipmentStatus.DEFEKT
                );
    }

    @Test
    void findByEquipmentIdOrderByChangedAtAsc_returnsEmptyListWhenNoHistory() {
        // Arrange
        var equipment = persistEquipment("INV-H-002");
        entityManager.clear();

        // Act
        var result = sut.findByEquipmentIdOrderByChangedAtAsc(equipment.getId());

        // Assert
        assertThat(result).isEmpty();
    }

    @Test
    void findByEquipmentIdOrderByChangedAtAsc_returnsOnlyHistoryForGivenEquipment() {
        // Arrange
        var eq1 = persistEquipment("INV-H-010");
        var eq2 = persistEquipment("INV-H-011");

        var h1 = new EquipmentHistory(eq1, null, EquipmentStatus.VERFUEGBAR);
        var h2 = new EquipmentHistory(eq2, null, EquipmentStatus.IN_GEBRAUCH);
        entityManager.persistAndFlush(h1);
        entityManager.persistAndFlush(h2);
        entityManager.clear();

        // Act
        var result = sut.findByEquipmentIdOrderByChangedAtAsc(eq1.getId());

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getNewStatus()).isEqualTo(EquipmentStatus.VERFUEGBAR);
    }

    @Test
    void findByEquipmentIdOrderByChangedAtAsc_firstEntryHasNullPreviousStatus() {
        // Arrange
        var equipment = persistEquipment("INV-H-020");
        var h1 = new EquipmentHistory(equipment, null, EquipmentStatus.VERFUEGBAR);
        entityManager.persistAndFlush(h1);
        entityManager.clear();

        // Act
        var result = sut.findByEquipmentIdOrderByChangedAtAsc(equipment.getId());

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getPreviousStatus()).isNull();
        assertThat(result.getFirst().getNewStatus()).isEqualTo(EquipmentStatus.VERFUEGBAR);
    }
}
