package de.thkoeln.ccq.firemanager.member;

import de.thkoeln.ccq.firemanager.location.Location;
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

@Tag("integration")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class MemberLocationAssignmentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MemberLocationAssignmentRepository sut;

    @Test
    void findByLocationId_returnsAssignmentsForLocation() {
        // Arrange
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        entityManager.persistAndFlush(location);
        
        MemberLocationAssignment assignment1 = new MemberLocationAssignment(UUID.randomUUID(), location);
        MemberLocationAssignment assignment2 = new MemberLocationAssignment(UUID.randomUUID(), location);
        
        entityManager.persistAndFlush(assignment1);
        entityManager.persistAndFlush(assignment2);
        entityManager.clear();

        // Act
        List<MemberLocationAssignment> result = sut.findByLocationId(location.getId());

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(assignment1, assignment2);
    }

    @Test
    void findByMemberId_returnsAssignmentsForMember() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        entityManager.persistAndFlush(location);
        
        MemberLocationAssignment assignment = new MemberLocationAssignment(memberId, location);
        entityManager.persistAndFlush(assignment);
        entityManager.clear();

        // Act
        List<MemberLocationAssignment> result = sut.findByMemberId(memberId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(assignment);
    }

    @Test
    void existsByMemberId_returnsTrueWhenAssignmentExists() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        entityManager.persistAndFlush(location);
        
        MemberLocationAssignment assignment = new MemberLocationAssignment(memberId, location);
        entityManager.persistAndFlush(assignment);
        entityManager.clear();

        // Act
        boolean result = sut.existsByMemberId(memberId);

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void existsByMemberId_returnsFalseWhenNoAssignmentExists() {
        // Arrange
        UUID nonExistentMemberId = UUID.randomUUID();

        // Act
        boolean result = sut.existsByMemberId(nonExistentMemberId);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void save_persistsAssignment() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        entityManager.persistAndFlush(location);
        
        MemberLocationAssignment assignment = new MemberLocationAssignment(memberId, location);

        // Act
        MemberLocationAssignment savedAssignment = sut.save(assignment);
        entityManager.clear();

        var foundAssignment = sut.findById(savedAssignment.getId());

        // Assert
        assertThat(foundAssignment).isPresent();
        assertThat(foundAssignment.get()).isEqualTo(assignment);
    }

    @Test
    void deleteById_removesAssignment() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "GERAETEHAUS");
        entityManager.persistAndFlush(location);
        
        MemberLocationAssignment assignment = new MemberLocationAssignment(memberId, location);
        entityManager.persistAndFlush(assignment);
        entityManager.clear();

        // Act
        sut.deleteById(assignment.getId());

        var result = sut.findById(assignment.getId());

        // Assert
        assertThat(result).isEmpty();
    }
}