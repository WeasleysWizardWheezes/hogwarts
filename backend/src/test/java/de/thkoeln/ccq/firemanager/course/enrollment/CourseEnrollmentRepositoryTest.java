package de.thkoeln.ccq.firemanager.course.enrollment;

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

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseEnrollmentRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseEnrollmentRepository sut;

    @Test
    void findAll_returnsPersistedEnrollments() {
        // Arrange
        var courseId = UUID.randomUUID();
        var enrollment = new CourseEnrollment(courseId, "Kurs", UUID.randomUUID(), "Teilnehmer 1", "PENDING", "Kommentar");
        entityManager.persistAndFlush(enrollment);
        entityManager.clear();

        // Act
        var result = sut.findAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getMemberName()).isEqualTo("Teilnehmer 1");
    }

    @Test
    void findByCourseId_returnsEnrollmentsForCourse() {
        // Arrange
        var courseId = UUID.randomUUID();
        var otherCourseId = UUID.randomUUID();
        
        var enrollment1 = new CourseEnrollment(courseId, "Kurs", UUID.randomUUID(), "Teilnehmer 1", "PENDING", "Kommentar 1");
        var enrollment2 = new CourseEnrollment(courseId, "Kurs", UUID.randomUUID(), "Teilnehmer 2", "CONFIRMED", "Kommentar 2");
        var enrollment3 = new CourseEnrollment(otherCourseId, "Anderer Kurs", UUID.randomUUID(), "Teilnehmer 3", "PENDING", "Kommentar 3");
        
        entityManager.persistAndFlush(enrollment1);
        entityManager.persistAndFlush(enrollment2);
        entityManager.persistAndFlush(enrollment3);
        entityManager.clear();

        // Act
        var result = sut.findByCourseId(courseId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CourseEnrollment::getMemberName)
                .containsExactlyInAnyOrder("Teilnehmer 1", "Teilnehmer 2");
    }

    @Test
    void findByMemberId_returnsEnrollmentsForMember() {
        // Arrange
        var memberId = UUID.randomUUID();
        var otherMemberId = UUID.randomUUID();
        
        var enrollment1 = new CourseEnrollment(UUID.randomUUID(), "Kurs 1", memberId, "Max Mustermann", "PENDING", "Kommentar 1");
        var enrollment2 = new CourseEnrollment(UUID.randomUUID(), "Kurs 2", memberId, "Max Mustermann", "CONFIRMED", "Kommentar 2");
        var enrollment3 = new CourseEnrollment(UUID.randomUUID(), "Kurs 3", otherMemberId, "Andere Person", "PENDING", "Kommentar 3");
        
        entityManager.persistAndFlush(enrollment1);
        entityManager.persistAndFlush(enrollment2);
        entityManager.persistAndFlush(enrollment3);
        entityManager.clear();

        // Act
        var result = sut.findByMemberId(memberId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(CourseEnrollment::getCourseId)
                .containsExactlyInAnyOrder(enrollment1.getCourseId(), enrollment2.getCourseId());
    }

    @Test
    void findById_returnsEnrollmentWhenIdExists() {
        // Arrange
        var enrollment = new CourseEnrollment(UUID.randomUUID(), "Kurs", UUID.randomUUID(), "Teilnehmer 1", "PENDING", "Kommentar");
        entityManager.persistAndFlush(enrollment);
        entityManager.clear();

        // Act
        var result = sut.findById(enrollment.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getMemberName()).isEqualTo("Teilnehmer 1");
    }

    @Test
    void save_createsNewEnrollment() {
        // Arrange
        var courseId = UUID.randomUUID();
        var enrollment = new CourseEnrollment(courseId, "Kurs", UUID.randomUUID(), "Teilnehmer 1", "PENDING", "Kommentar");

        // Act
        var savedEnrollment = sut.save(enrollment);

        // Assert
        assertThat(savedEnrollment.getId()).isNotNull();
        assertThat(savedEnrollment.getMemberName()).isEqualTo("Teilnehmer 1");
    }

    @Test
    void deleteById_removesEnrollment() {
        // Arrange
        var enrollment = new CourseEnrollment(UUID.randomUUID(), "Kurs", UUID.randomUUID(), "Teilnehmer 1", "PENDING", "Kommentar");
        entityManager.persistAndFlush(enrollment);
        var enrollmentId = enrollment.getId();
        entityManager.clear();

        // Act
        sut.deleteById(enrollmentId);

        // Assert
        assertThat(sut.findById(enrollmentId)).isEmpty();
    }
}