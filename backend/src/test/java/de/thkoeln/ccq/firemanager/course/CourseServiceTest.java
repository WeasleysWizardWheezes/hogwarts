package de.thkoeln.ccq.firemanager.course;

import de.thkoeln.ccq.firemanager.course.exception.CourseNotFoundException;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CourseServiceTest {

    @Mock
    private CourseRepository courseRepositoryStub;

    @InjectMocks
    private CourseService sut;

    @Test
    void create_returnsCreatedCourse() {
        // Arrange
        var now = OffsetDateTime.now();
        var course = new Course(
                "Feuerwehr-Grundausbildung",
                "Grundlagenschulung für neue Mitglieder",
                20,
                UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                "Max Mustermann",
                now.plusDays(1),
                now.plusDays(5)
        );
        when(courseRepositoryStub.save(any(Course.class))).thenReturn(course);

        // Act
        var result = sut.create(
                "Feuerwehr-Grundausbildung",
                "Grundlagenschulung für neue Mitglieder",
                20,
                UUID.fromString("12345678-1234-1234-1234-123456789abc"),
                "Max Mustermann",
                now.plusDays(1),
                now.plusDays(5)
        );

        // Assert
        assertThat(result.getName()).isEqualTo("Feuerwehr-Grundausbildung");
        assertThat(result.getMaxParticipants()).isEqualTo(20);
        verify(courseRepositoryStub).save(any(Course.class));
    }

    @Test
    void getAll_returnsAllCourses() {
        // Arrange
        var course1 = new Course("Kurs 1", "Beschreibung 1", 10, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        var course2 = new Course("Kurs 2", "Beschreibung 2", 15, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        when(courseRepositoryStub.findAll()).thenReturn(List.of(course1, course2));

        // Act
        var result = sut.getAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("Kurs 1");
        assertThat(result.get(1).getName()).isEqualTo("Kurs 2");
    }

    @Test
    void getById_returnsCourseWhenIdExists() {
        // Arrange
        var course = new Course("Feuerwehr-Grundausbildung", "Beschreibung", 20, UUID.randomUUID(), "Max Mustermann", OffsetDateTime.now(), OffsetDateTime.now());
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));

        // Act
        var result = sut.getById(course.getId());

        // Assert
        assertThat(result.getName()).isEqualTo("Feuerwehr-Grundausbildung");
    }

    @Test
    void getById_throwsCourseNotFoundExceptionWhenNotFound() {
        // Arrange
        var courseId = UUID.randomUUID();
        when(courseRepositoryStub.findById(courseId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(courseId))
                .isInstanceOf(CourseNotFoundException.class)
                .hasMessageContaining(courseId.toString());
    }

    @Test
    void update_returnsUpdatedCourse() {
        // Arrange
        var existingCourse = new Course("Alte Bezeichnung", "Alte Beschreibung", 10, UUID.randomUUID(), "Alter Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        var updatedCourse = new Course("Neue Bezeichnung", "Neue Beschreibung", 20, UUID.randomUUID(), "Neuer Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        when(courseRepositoryStub.findById(existingCourse.getId())).thenReturn(Optional.of(existingCourse));
        when(courseRepositoryStub.save(any(Course.class))).thenReturn(updatedCourse);

        // Act
        var result = sut.update(
                existingCourse.getId(),
                "Neue Bezeichnung",
                "Neue Beschreibung",
                20,
                UUID.fromString("87654321-4321-4321-4321-cba987654321"),
                "Neuer Instructor",
                OffsetDateTime.now().plusDays(1),
                OffsetDateTime.now().plusDays(5),
                "PLANNED"
        );

        // Assert
        assertThat(result.getName()).isEqualTo("Neue Bezeichnung");
        assertThat(result.getMaxParticipants()).isEqualTo(20);
    }

    @Test
    void deleteById_deletesCourse() {
        // Arrange
        var courseId = UUID.randomUUID();
        when(courseRepositoryStub.existsById(courseId)).thenReturn(true);

        // Act
        sut.deleteById(courseId);

        // Assert
        verify(courseRepositoryStub).deleteById(courseId);
    }

    @Test
    void deleteById_throwsWhenNotFound() {
        // Arrange
        var courseId = UUID.randomUUID();
        when(courseRepositoryStub.existsById(courseId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> sut.deleteById(courseId))
                .isInstanceOf(CourseNotFoundException.class);
    }

    @Test
    void validateEnrollmentCapacity_throwsWhenCourseIsFull() {
        // Arrange
        var course = new Course("Voller Kurs", "Beschreibung", 10, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants(); // Jetzt 10 Teilnehmer
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));

        // Act & Assert
        assertThatThrownBy(() -> sut.validateEnrollmentCapacity(course.getId()))
                .isInstanceOf(de.thkoeln.ccq.firemanager.course.exception.CourseConflictException.class)
                .hasMessageContaining("Course is full");
    }

    @Test
    void validateEnrollmentCapacity_doesNotThrowWhenCourseHasCapacity() {
        // Arrange
        var course = new Course("Kurs mit Platz", "Beschreibung", 10, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        course.incrementCurrentParticipants();
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));

        // Act & Assert - darf keine Exception werfen
        sut.validateEnrollmentCapacity(course.getId());
    }

    @Test
    void incrementCurrentParticipants_updatesCourse() {
        // Arrange
        var course = new Course("Kurs", "Beschreibung", 10, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));
        when(courseRepositoryStub.save(any(Course.class))).thenReturn(course);

        // Act
        sut.incrementCurrentParticipants(course.getId());

        // Assert
        assertThat(course.getCurrentParticipants()).isEqualTo(1);
    }

    @Test
    void incrementWaitingListCount_updatesCourse() {
        // Arrange
        var course = new Course("Kurs", "Beschreibung", 10, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));
        when(courseRepositoryStub.save(any(Course.class))).thenReturn(course);

        // Act
        sut.incrementWaitingListCount(course.getId());

        // Assert
        assertThat(course.getWaitingListCount()).isEqualTo(1);
    }

    @Test
    void decrementCurrentParticipants_updatesCourse() {
        // Arrange
        var course = new Course("Kurs", "Beschreibung", 10, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        course.incrementCurrentParticipants();
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));
        when(courseRepositoryStub.save(any(Course.class))).thenReturn(course);

        // Act
        sut.decrementCurrentParticipants(course.getId());

        // Assert
        assertThat(course.getCurrentParticipants()).isEqualTo(0);
    }

    @Test
    void decrementWaitingListCount_updatesCourse() {
        // Arrange
        var course = new Course("Kurs", "Beschreibung", 10, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now());
        course.incrementWaitingListCount();
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));
        when(courseRepositoryStub.save(any(Course.class))).thenReturn(course);

        // Act
        sut.decrementWaitingListCount(course.getId());

        // Assert
        assertThat(course.getWaitingListCount()).isEqualTo(0);
    }
}
