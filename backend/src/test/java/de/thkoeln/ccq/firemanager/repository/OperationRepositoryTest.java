package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.Operation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class OperationRepositoryTest {

    @Autowired
    private OperationRepository sut;

    @BeforeEach
    void cleanDatabase() {
        sut.deleteAll();
    }

    @Test
    void shouldSaveAndFindOperation() {
        // Arrange
        var operation = new Operation(
            "Einsatz Feuerwehr",
            "Brandbekämpfung",
            LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(3),
            List.of(UUID.randomUUID()),
            "Brand in Bürogebäude"
        );

        // Act
        var saved = sut.save(operation);
        var found = sut.findById(saved.getId());

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Einsatz Feuerwehr");
        assertThat(found.get().getOperationType()).isEqualTo("Brandbekämpfung");
    }

    @Test
    void shouldFindAllOperations() {
        // Arrange
        var operation1 = new Operation(
            "Einsatz 1",
            "Brandbekämpfung",
            LocalDateTime.now(),
            LocalDateTime.now(),
            List.of(),
            ""
        );
        var operation2 = new Operation(
            "Einsatz 2",
            "Hilfeleistung",
            LocalDateTime.now(),
            LocalDateTime.now(),
            List.of(),
            ""
        );
        sut.save(operation1);
        sut.save(operation2);

        // Act
        var result = sut.findAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.stream().map(Operation::getTitle)).containsExactlyInAnyOrder("Einsatz 1", "Einsatz 2");
    }

    @Test
    void shouldDeleteOperation() {
        // Arrange
        var operation = new Operation(
            "Einsatz zum Löschen",
            "Brandbekämpfung",
            LocalDateTime.now(),
            LocalDateTime.now(),
            List.of(),
            ""
        );
        var saved = sut.save(operation);
        var id = saved.getId();

        // Act
        sut.deleteById(id);
        var found = sut.findById(id);

        // Assert
        assertThat(found).isEmpty();
    }

    @Test
    void shouldHandleEmptyDatabase() {
        // Act
        var result = sut.findAll();

        // Assert
        assertThat(result).isEmpty();
    }
}