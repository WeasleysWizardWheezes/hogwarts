package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import de.thkoeln.ccq.firemanager.vehicle.VehicleStatus;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
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

    @Autowired
    private EquipmentHistoryRepository equipmentHistoryRepository;

    @Test
    void findAllWithFilters_appliesSearchRelationshipsStatusAndDueDate() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", "Atemschutzgeräte");
        var otherCategory = new EquipmentCategory("Funk", "Funkgeräte");
        var vehicleGroup = new VehicleGroup("Löschfahrzeuge", "Beschreibung");
        entityManager.persistAndFlush(category);
        entityManager.persistAndFlush(otherCategory);
        entityManager.persistAndFlush(vehicleGroup);
        var vehicle = new Vehicle("LF 10/6", "Florian Köln 1-46-1", "K-AB 1234",
                2020, "Löschgruppenfahrzeug", VehicleStatus.VERFUEGBAR, vehicleGroup);
        entityManager.persistAndFlush(vehicle);

        var matching = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.WARTUNG, category, vehicle,
                LocalDate.of(2026, 6, 15), null);
        var wrongStatus = new Equipment(
                "Pressluftatmer PA 301", "AGT-2024-0043", null,
                EquipmentStatus.VERFUEGBAR, category, vehicle,
                LocalDate.of(2026, 6, 15), null);
        var wrongCategory = new Equipment(
                "Pressluftatmer PA 302", "AGT-2024-0044", null,
                EquipmentStatus.WARTUNG, otherCategory, vehicle,
                LocalDate.of(2026, 6, 15), null);
        var archived = new Equipment(
                "Pressluftatmer PA 303", "AGT-2024-0045", null,
                EquipmentStatus.WARTUNG, category, vehicle,
                LocalDate.of(2026, 6, 15), null);
        archived.setArchived(true);
        entityManager.persistAndFlush(matching);
        entityManager.persistAndFlush(wrongStatus);
        entityManager.persistAndFlush(wrongCategory);
        entityManager.persistAndFlush(archived);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters(
                "AGT-2024", category.getId(), vehicle.getId(), EquipmentStatus.WARTUNG,
                LocalDate.of(2026, 6, 30), PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent())
                .extracting(Equipment::getId)
                .containsExactly(matching.getId());
    }

    @Test
    void findAllWithFilters_matchesDueInspectionOrMaintenanceDate() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        entityManager.persistAndFlush(category);
        var inspectionDue = new Equipment(
                "Prüfgerät", "PR-001", null, EquipmentStatus.VERFUEGBAR,
                category, null, LocalDate.of(2026, 6, 15), null);
        var maintenanceDue = new Equipment(
                "Wartungsgerät", "WA-001", null, EquipmentStatus.VERFUEGBAR,
                category, null, null, LocalDate.of(2026, 6, 20));
        var notDue = new Equipment(
                "Neues Gerät", "NEU-001", null, EquipmentStatus.VERFUEGBAR,
                category, null, LocalDate.of(2026, 8, 1), LocalDate.of(2026, 8, 2));
        entityManager.persistAndFlush(inspectionDue);
        entityManager.persistAndFlush(maintenanceDue);
        entityManager.persistAndFlush(notDue);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters(
                null, null, null, null, LocalDate.of(2026, 6, 30), PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent())
                .extracting(Equipment::getId)
                .containsExactlyInAnyOrder(inspectionDue.getId(), maintenanceDue.getId());
    }

    @Test
    void findAllWithFilters_searchesNameAndInventoryNumber() {
        // Arrange
        var category = new EquipmentCategory("Funk", null);
        entityManager.persistAndFlush(category);
        var nameMatch = new Equipment(
                "Handfunkgerät", "F-001", null, EquipmentStatus.VERFUEGBAR,
                category, null, null, null);
        var inventoryMatch = new Equipment(
                "Funkgerät", "SPECIAL-42", null, EquipmentStatus.VERFUEGBAR,
                category, null, null, null);
        var noMatch = new Equipment(
                "Atemschutzmaske", "A-001", null, EquipmentStatus.VERFUEGBAR,
                category, null, null, null);
        entityManager.persistAndFlush(nameMatch);
        entityManager.persistAndFlush(inventoryMatch);
        entityManager.persistAndFlush(noMatch);
        entityManager.clear();

        // Act
        var result = sut.findAllWithFilters(
                "special-42", null, null, null, null, PageRequest.of(0, 20));

        // Assert
        assertThat(result.getContent())
                .extracting(Equipment::getId)
                .containsExactly(inventoryMatch.getId());
    }

    @Test
    void save_persistsEquipmentHistoryAndFindsItChronologically() {
        // Arrange
        var category = new EquipmentCategory("Atemschutz", null);
        entityManager.persistAndFlush(category);
        var equipment = new Equipment(
                "Pressluftatmer PA 300", "AGT-2024-0042", null,
                EquipmentStatus.VERFUEGBAR, category, null, null, null);
        entityManager.persistAndFlush(equipment);
        var first = new EquipmentHistory(equipment, EquipmentStatus.VERFUEGBAR, EquipmentStatus.WARTUNG);
        var second = new EquipmentHistory(equipment, EquipmentStatus.WARTUNG, EquipmentStatus.DEFEKT);

        // Act
        equipmentHistoryRepository.save(first);
        equipmentHistoryRepository.saveAndFlush(second);
        entityManager.clear();
        var result = equipmentHistoryRepository.findByEquipmentIdOrderByChangedAtAsc(equipment.getId());

        // Assert
        assertThat(result)
                .extracting(EquipmentHistory::getNewStatus)
                .containsExactly(EquipmentStatus.WARTUNG, EquipmentStatus.DEFEKT);
    }
}