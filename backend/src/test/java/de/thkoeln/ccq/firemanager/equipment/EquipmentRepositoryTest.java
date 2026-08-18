package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class EquipmentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EquipmentRepository sut;

    // ─── Hilfsmethoden ───────────────────────────────────────────────────────

    private EquipmentCategory persistCategory(String name) {
        var category = new EquipmentCategory(name, null);
        return entityManager.persistAndFlush(category);
    }

    private Vehicle persistVehicle(String name) {
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(vehicleGroup);
        var vehicle = new Vehicle(name, "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Beschreibung", VehicleStatus.VERFUEGBAR, vehicleGroup);
        return entityManager.persistAndFlush(vehicle);
    }

    private Equipment persistEquipment(String name, String inventoryNumber,
            EquipmentCategory category, EquipmentStatus status) {
        var equipment = new Equipment(name, inventoryNumber, null, status, category, null, null, null);
        return entityManager.persistAndFlush(equipment);
    }

    // ─── findAllWithFilters ───────────────────────────────────────────────────

    @Test
    void findAllWithFilters_returnsAllNonArchivedEquipment() {
        // Arrange
        var category = persistCategory("Atemschutz");
        persistEquipment("Atemschutzgerät PA 300", "INV-001", category, EquipmentStatus.VERFUEGBAR);
        persistEquipment("Wärmebildkamera", "INV-002", category, EquipmentStatus.VERFUEGBAR);
        var archived = new Equipment("Altes Gerät", "INV-003", null, EquipmentStatus.ARCHIVIERT, category, null, null, null);
        archived.setArchived(true);
        entityManager.persistAndFlush(archived);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters(null, null, null, null, null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent())
                .extracting(Equipment::getInventoryNumber)
                .containsExactlyInAnyOrder("INV-001", "INV-002");
    }

    @Test
    void findAllWithFilters_searchByName() {
        // Arrange
        var category = persistCategory("Atemschutz");
        persistEquipment("Atemschutzgerät PA 300", "INV-010", category, EquipmentStatus.VERFUEGBAR);
        persistEquipment("Wärmebildkamera TIC 360", "INV-011", category, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters("Atemschutz", null, null, null, null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getName()).isEqualTo("Atemschutzgerät PA 300");
    }

    @Test
    void findAllWithFilters_searchByInventoryNumber() {
        // Arrange
        var category = persistCategory("Atemschutz");
        persistEquipment("Atemschutzgerät PA 300", "AT-2024-001", category, EquipmentStatus.VERFUEGBAR);
        persistEquipment("Wärmebildkamera TIC 360", "WB-2024-001", category, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters("AT-2024", null, null, null, null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getInventoryNumber()).isEqualTo("AT-2024-001");
    }

    @Test
    void findAllWithFilters_searchIsCaseInsensitive() {
        // Arrange
        var category = persistCategory("Atemschutz");
        persistEquipment("Atemschutzgerät PA 300", "INV-020", category, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters("atemschutz", null, null, null, null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findAllWithFilters_filtersByCategoryId() {
        // Arrange
        var cat1 = persistCategory("Atemschutz");
        var cat2 = persistCategory("Werkzeug");
        persistEquipment("Atemschutzgerät PA 300", "INV-030", cat1, EquipmentStatus.VERFUEGBAR);
        persistEquipment("Säbelsäge", "INV-031", cat2, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters(null, cat1.getId(), null, null, null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getInventoryNumber()).isEqualTo("INV-030");
    }

    @Test
    void findAllWithFilters_filtersByStatus() {
        // Arrange
        var category = persistCategory("Atemschutz");
        persistEquipment("Atemschutzgerät PA 300", "INV-040", category, EquipmentStatus.VERFUEGBAR);
        persistEquipment("Defektes Gerät", "INV-041", category, EquipmentStatus.DEFEKT);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters(null, null, null, EquipmentStatus.DEFEKT, null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getInventoryNumber()).isEqualTo("INV-041");
    }

    @Test
    void findAllWithFilters_filtersByVehicleId() {
        // Arrange
        var category = persistCategory("Atemschutz");
        var vehicle = persistVehicle("LF 10/6");

        var eq1 = new Equipment("Atemschutzgerät PA 300", "INV-050", null,
                EquipmentStatus.VERFUEGBAR, category, vehicle, null, null);
        entityManager.persistAndFlush(eq1);
        persistEquipment("Frestehendes Gerät", "INV-051", category, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters(null, null, vehicle.getId(), null, null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getInventoryNumber()).isEqualTo("INV-050");
    }

    @Test
    void findAllWithFilters_filtersByDueBeforeInspectionDate() {
        // Arrange
        var category = persistCategory("Atemschutz");
        var eq1 = new Equipment("Gerät fällig", "INV-060", null,
                EquipmentStatus.VERFUEGBAR, category, null,
                LocalDate.of(2025, 6, 1), null);
        var eq2 = new Equipment("Gerät nicht fällig", "INV-061", null,
                EquipmentStatus.VERFUEGBAR, category, null,
                LocalDate.of(2026, 1, 1), null);
        entityManager.persistAndFlush(eq1);
        entityManager.persistAndFlush(eq2);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters(null, null, null, null,
                LocalDate.of(2025, 12, 31), PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().getFirst().getInventoryNumber()).isEqualTo("INV-060");
    }

    @Test
    void findAllWithFilters_filtersByDueBeforeMaintenanceDate() {
        // Arrange
        var category = persistCategory("Atemschutz");
        var eq1 = new Equipment("Gerät mit Wartung fällig", "INV-070", null,
                EquipmentStatus.VERFUEGBAR, category, null,
                null, LocalDate.of(2025, 3, 15));
        entityManager.persistAndFlush(eq1);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters(null, null, null, null,
                LocalDate.of(2025, 12, 31), PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void findAllWithFilters_returnsEmptyWhenNoMatch() {
        // Arrange
        var category = persistCategory("Atemschutz");
        persistEquipment("Atemschutzgerät PA 300", "INV-080", category, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters("Nichtexistent", null, null, null, null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getTotalElements()).isEqualTo(0);
    }

    // ─── existsByInventoryNumberAndArchivedFalse ──────────────────────────────

    @Test
    void existsByInventoryNumberAndArchivedFalse_returnsTrueWhenActiveEquipmentExists() {
        // Arrange
        var category = persistCategory("Atemschutz");
        persistEquipment("Atemschutzgerät PA 300", "INV-100", category, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act
        var result = sut.existsByInventoryNumberAndArchivedFalse("INV-100");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void existsByInventoryNumberAndArchivedFalse_returnsFalseWhenEquipmentArchived() {
        // Arrange
        var category = persistCategory("Atemschutz");
        var equipment = new Equipment("Altes Gerät", "INV-101", null,
                EquipmentStatus.ARCHIVIERT, category, null, null, null);
        equipment.setArchived(true);
        entityManager.persistAndFlush(equipment);
        entityManager.clear();

        // Act
        var result = sut.existsByInventoryNumberAndArchivedFalse("INV-101");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void existsByInventoryNumberAndArchivedFalse_returnsFalseWhenNotFound() {
        // Act
        var result = sut.existsByInventoryNumberAndArchivedFalse("NICHT-VORHANDEN");

        // Assert
        assertThat(result).isFalse();
    }

    // ─── existsByInventoryNumberAndIdNotAndArchivedFalse ─────────────────────

    @Test
    void existsByInventoryNumberAndIdNotAndArchivedFalse_returnsTrueWhenAnotherEquipmentHasSameNumber() {
        // Arrange
        var category = persistCategory("Atemschutz");
        var eq1 = persistEquipment("Gerät A", "INV-200", category, EquipmentStatus.VERFUEGBAR);
        var eq2 = persistEquipment("Gerät B", "INV-201", category, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act – Gerät B versucht INV-200 zu übernehmen (gehört Gerät A)
        var result = sut.existsByInventoryNumberAndIdNotAndArchivedFalse("INV-200", eq2.getId());

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void existsByInventoryNumberAndIdNotAndArchivedFalse_returnsFalseForOwnInventoryNumber() {
        // Arrange
        var category = persistCategory("Atemschutz");
        var eq1 = persistEquipment("Gerät A", "INV-210", category, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act – Gerät A behält seine eigene Inventarnummer
        var result = sut.existsByInventoryNumberAndIdNotAndArchivedFalse("INV-210", eq1.getId());

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void existsByInventoryNumberAndIdNotAndArchivedFalse_returnsFalseWhenConflictingEquipmentIsArchived() {
        // Arrange
        var category = persistCategory("Atemschutz");
        var archivedEq = new Equipment("Altes Gerät", "INV-220", null,
                EquipmentStatus.ARCHIVIERT, category, null, null, null);
        archivedEq.setArchived(true);
        entityManager.persistAndFlush(archivedEq);
        var activeEq = persistEquipment("Neues Gerät", "INV-221", category, EquipmentStatus.VERFUEGBAR);
        entityManager.clear();

        // Act – aktives Gerät versucht INV-220 zu verwenden (nur archiviertes Gerät hat es)
        var result = sut.existsByInventoryNumberAndIdNotAndArchivedFalse("INV-220", activeEq.getId());

        // Assert
        assertThat(result).isFalse();
    }
}
