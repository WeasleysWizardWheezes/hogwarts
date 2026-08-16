package de.thkoeln.ccq.firemanager.course;

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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Tag("integration")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CourseRepositoryTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CourseRepository sut;

    @Test
    void findAll_returnsPersistedCourses() {
        // Arrange
        var instructorId = UUID.randomUUID();
        var course = new Course("Kurs 1", "Beschreibung 1", 5, instructorId, "Instructor 1", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        entityManager.persistAndFlush(course);
        entityManager.clear();

        // Act
        var result = sut.findAll();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getName()).isEqualTo("Kurs 1");
    }

    @Test
    void findById_returnsCourseWhenIdExists() {
        // Arrange
        var instructorId = UUID.randomUUID();
        var course = new Course("Kurs 1", "Beschreibung 1", 5, instructorId, "Instructor 1", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        entityManager.persistAndFlush(course);
        entityManager.clear();

        // Act
        var result = sut.findById(course.getId());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Kurs 1");
    }

    @Test
    void existsById_returnsTrueWhenCourseExists() {
        // Arrange
        var instructorId = UUID.randomUUID();
        var course = new Course("Kurs 1", "Beschreibung 1", 5, instructorId, "Instructor 1", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        entityManager.persistAndFlush(course);
        entityManager.clear();

        // Act
        var exists = sut.existsById(course.getId());

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void save_createsNewCourse() {
        // Arrange
        var instructorId = UUID.randomUUID();
        var course = new Course("Neuer Kurs", "Neue Beschreibung", 10, instructorId, "Neuer Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));

        // Act
        var savedCourse = sut.save(course);

        // Assert
        assertThat(savedCourse.getId()).isNotNull();
        assertThat(savedCourse.getName()).isEqualTo("Neuer Kurs");
        assertThat(savedCourse.getMaxParticipants()).isEqualTo(10);
    }

    @Test
    void deleteById_removesCourse() {
        // Arrange
        var instructorId = UUID.randomUUID();
        var course = new Course("Zu löschender Kurs", "Beschreibung", 5, instructorId, "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        entityManager.persistAndFlush(course);
        var courseId = course.getId();
        entityManager.clear();

        // Act
        sut.deleteById(courseId);

        // Assert
        assertThat(sut.findById(courseId)).isEmpty();
    }
}