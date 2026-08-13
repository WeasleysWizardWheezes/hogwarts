package de.thkoeln.ccq.firemanager.course;

import de.thkoeln.ccq.firemanager.course.exception.CourseConflictException;
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
import static org.mockito.Mockito.doThrow;
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
        var instructorId = UUID.randomUUID();
        var startDate = OffsetDateTime.now();
        var endDate = startDate.plusDays(7);
        var course = new Course(
                "Atemschutzkurs",
                "Grundausbildung Atemschutz",
                10,
                instructorId,
                "Max Mustermann",
                startDate,
                endDate,
                "PLANNED"
        );
        when(courseRepositoryStub.save(any(Course.class))).thenReturn(course);

        // Act
        var result = sut.create(
                "Atemschutzkurs",
                "Grundausbildung Atemschutz",
                10,
                instructorId,
                "Max Mustermann",
                startDate,
                endDate,
                "PLANNED"
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Atemschutzkurs");
    }

    @Test
    void getAll_returnsAllCourses() {
        // Arrange
        var course1 = new Course("Kurs 1", "Beschreibung 1", 5, UUID.randomUUID(), "Instructor 1", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        var course2 = new Course("Kurs 2", "Beschreibung 2", 5, UUID.randomUUID(), "Instructor 2", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        when(courseRepositoryStub.findAll()).thenReturn(List.of(course1, course2));

        // Act
        var result = sut.getAll();

        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void getById_returnsCourseWhenIdExists() {
        // Arrange
        var course = new Course("Kurs", "Beschreibung", 5, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));

        // Act
        var result = sut.getById(course.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Kurs");
    }

    @Test
    void getById_throwsExceptionWhenNotFound() {
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
        var course = new Course("Alter Kurs", "Alte Beschreibung", 5, UUID.randomUUID(), "Alter Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        var updatedCourse = new Course("Neuer Kurs", "Neue Beschreibung", 10, course.getInstructorId(), "Neuer Instructor", course.getStartDate(), course.getEndDate().plusDays(7));
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));
        when(courseRepositoryStub.save(any(Course.class))).thenReturn(updatedCourse);

        // Act
        var result = sut.update(
                course.getId(),
                "Neuer Kurs",
                "Neue Beschreibung",
                10,
                course.getInstructorId(),
                "Neuer Instructor",
                course.getStartDate(),
                course.getEndDate().plusDays(7),
                "PLANNED"
        );

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Neuer Kurs");
        assertThat(result.getMaxParticipants()).isEqualTo(10);
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
    void validateEnrollmentCapacity_throwsExceptionWhenFull() {
        // Arrange
        var courseId = UUID.randomUUID();
        var course = new Course("Voller Kurs", "Beschreibung", 2, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        course.incrementCurrentParticipants();
        course.incrementCurrentParticipants();
        when(courseRepositoryStub.findById(courseId)).thenReturn(Optional.of(course));

        // Act & Assert
        assertThatThrownBy(() -> sut.validateEnrollmentCapacity(courseId))
                .isInstanceOf(CourseConflictException.class)
                .hasMessageContaining("Course is full");
    }

    @Test
    void incrementCurrentParticipants_updatesCourse() {
        // Arrange
        var course = new Course("Kurs", "Beschreibung", 5, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
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
        var course = new Course("Kurs", "Beschreibung", 5, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
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
        var course = new Course("Kurs", "Beschreibung", 5, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
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
        var course = new Course("Kurs", "Beschreibung", 5, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        course.incrementWaitingListCount();
        when(courseRepositoryStub.findById(course.getId())).thenReturn(Optional.of(course));
        when(courseRepositoryStub.save(any(Course.class))).thenReturn(course);

        // Act
        sut.decrementWaitingListCount(course.getId());

        // Assert
        assertThat(course.getWaitingListCount()).isEqualTo(0);
    }
}