package de.thkoeln.ccq.firemanager.course.enrollment;

import de.thkoeln.ccq.firemanager.course.Course;
import de.thkoeln.ccq.firemanager.course.exception.CourseConflictException;
import de.thkoeln.ccq.firemanager.course.enrollment.exception.CourseEnrollmentNotFoundException;
import de.thkoeln.ccq.firemanager.course.CourseService;
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
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class CourseEnrollmentServiceTest {

    @Mock
    private CourseEnrollmentRepository courseEnrollmentRepositoryStub;

    @Mock
    private CourseService courseServiceStub;

    @InjectMocks
    private CourseEnrollmentService sut;

    @Test
    void create_returnsCreatedEnrollment_whenCourseHasCapacity() {
        // Arrange
        var courseId = UUID.randomUUID();
        var memberId = UUID.randomUUID();
        var course = new Course("Kurs", "Beschreibung", 5, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        var enrollment = new CourseEnrollment(courseId, "Kurs", memberId, "Max Mustermann", "PENDING", "Kommentar");

        when(courseServiceStub.getById(courseId)).thenReturn(course);
        doAnswer(invocation -> {
            // validateEnrollmentCapacity ist void - einfach nichts tun
            return null;
        }).when(courseServiceStub).validateEnrollmentCapacity(courseId);
        when(courseEnrollmentRepositoryStub.save(any(CourseEnrollment.class))).thenReturn(enrollment);

        // Act
        var result = sut.create(courseId, memberId, "Max Mustermann", "PENDING", "Kommentar");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMemberName()).isEqualTo("Max Mustermann");
    }

    @Test
    void create_throwsException_whenCourseIsFull() {
        // Arrange
        var courseId = UUID.randomUUID();
        var memberId = UUID.randomUUID();

        doThrow(new CourseConflictException("Course is full. Maximum participants reached."))
                .when(courseServiceStub)
                .validateEnrollmentCapacity(any(UUID.class));

        // Act & Assert
        assertThatThrownBy(() -> sut.create(courseId, memberId, "Max Mustermann", "PENDING", "Kommentar"))
                .isInstanceOf(CourseConflictException.class)
                .hasMessageContaining("Course is full");
    }

    @Test
    void getAllByCourse_returnsAllEnrollments() {
        // Arrange
        var courseId = UUID.randomUUID();
        var enrollment1 = new CourseEnrollment(courseId, "Kurs", UUID.randomUUID(), "Teilnehmer 1", "PENDING", "Kommentar 1");
        var enrollment2 = new CourseEnrollment(courseId, "Kurs", UUID.randomUUID(), "Teilnehmer 2", "CONFIRMED", "Kommentar 2");
        when(courseEnrollmentRepositoryStub.findByCourseId(courseId)).thenReturn(List.of(enrollment1, enrollment2));

        // Act
        var result = sut.getAllByCourse(courseId);

        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void getAllByMember_returnsAllEnrollments() {
        // Arrange
        var memberId = UUID.randomUUID();
        var enrollment1 = new CourseEnrollment(UUID.randomUUID(), "Kurs 1", memberId, "Max Mustermann", "PENDING", "Kommentar 1");
        var enrollment2 = new CourseEnrollment(UUID.randomUUID(), "Kurs 2", memberId, "Max Mustermann", "CONFIRMED", "Kommentar 2");
        when(courseEnrollmentRepositoryStub.findByMemberId(memberId)).thenReturn(List.of(enrollment1, enrollment2));

        // Act
        var result = sut.getAllByMember(memberId);

        // Assert
        assertThat(result).hasSize(2);
    }

    @Test
    void getById_returnsEnrollmentWhenIdExists() {
        // Arrange
        var enrollment = new CourseEnrollment(UUID.randomUUID(), "Kurs", UUID.randomUUID(), "Max Mustermann", "PENDING", "Kommentar");
        when(courseEnrollmentRepositoryStub.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));

        // Act
        var result = sut.getById(enrollment.getId());

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMemberName()).isEqualTo("Max Mustermann");
    }

    @Test
    void getById_throwsExceptionWhenNotFound() {
        // Arrange
        var enrollmentId = UUID.randomUUID();
        when(courseEnrollmentRepositoryStub.findById(enrollmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(enrollmentId))
                .isInstanceOf(CourseEnrollmentNotFoundException.class)
                .hasMessageContaining(enrollmentId.toString());
    }

    @Test
    void cancelEnrollment_updatesEnrollmentStatus_whenConfirmed() {
        // Arrange
        var enrollment = new CourseEnrollment(UUID.randomUUID(), "Kurs", UUID.randomUUID(), "Max Mustermann", "CONFIRMED", "Kommentar");
        when(courseEnrollmentRepositoryStub.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        doAnswer(invocation -> {
            // decrementCurrentParticipants ist void
            return null;
        }).when(courseServiceStub).decrementCurrentParticipants(enrollment.getCourseId());
        when(courseEnrollmentRepositoryStub.save(any(CourseEnrollment.class))).thenReturn(enrollment);

        // Act
        sut.cancelEnrollment(enrollment.getId());

        // Assert
        assertThat(enrollment.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void cancelEnrollment_updatesEnrollmentStatus_whenWaitingList() {
        // Arrange
        var enrollment = new CourseEnrollment(UUID.randomUUID(), "Kurs", UUID.randomUUID(), "Max Mustermann", "WAITING_LIST", "Kommentar");
        when(courseEnrollmentRepositoryStub.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        doAnswer(invocation -> {
            // decrementWaitingListCount ist void
            return null;
        }).when(courseServiceStub).decrementWaitingListCount(enrollment.getCourseId());
        when(courseEnrollmentRepositoryStub.save(any(CourseEnrollment.class))).thenReturn(enrollment);

        // Act
        sut.cancelEnrollment(enrollment.getId());

        // Assert
        assertThat(enrollment.getStatus()).isEqualTo("CANCELLED");
    }

    @Test
    void deleteById_deletesEnrollment() {
        // Arrange
        var enrollment = new CourseEnrollment(UUID.randomUUID(), "Kurs", UUID.randomUUID(), "Max Mustermann", "CONFIRMED", "Kommentar");
        when(courseEnrollmentRepositoryStub.findById(enrollment.getId())).thenReturn(Optional.of(enrollment));
        doAnswer(invocation -> {
            // decrementCurrentParticipants ist void
            return null;
        }).when(courseServiceStub).decrementCurrentParticipants(enrollment.getCourseId());

        // Act
        sut.deleteById(enrollment.getId());

        // Assert
        verify(courseEnrollmentRepositoryStub).deleteById(enrollment.getId());
    }

    @Test
    void create_confirmedEnrollment_incrementsCurrentParticipants() {
        // Arrange
        var courseId = UUID.randomUUID();
        var memberId = UUID.randomUUID();
        var course = new Course("Kurs", "Beschreibung", 5, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        var enrollment = new CourseEnrollment(courseId, "Kurs", memberId, "Max Mustermann", "CONFIRMED", "Kommentar");

        when(courseServiceStub.getById(courseId)).thenReturn(course);
        doAnswer(invocation -> {
            // validateEnrollmentCapacity ist void
            return null;
        }).when(courseServiceStub).validateEnrollmentCapacity(courseId);
        when(courseEnrollmentRepositoryStub.save(any(CourseEnrollment.class))).thenReturn(enrollment);
        doAnswer(invocation -> {
            // incrementCurrentParticipants ist void
            return null;
        }).when(courseServiceStub).incrementCurrentParticipants(courseId);

        // Act
        var result = sut.create(courseId, memberId, "Max Mustermann", "CONFIRMED", "Kommentar");

        // Assert
        assertThat(result).isNotNull();
    }

    @Test
    void create_waitingListEnrollment_incrementsWaitingListCount() {
        // Arrange
        var courseId = UUID.randomUUID();
        var memberId = UUID.randomUUID();
        var course = new Course("Kurs", "Beschreibung", 5, UUID.randomUUID(), "Instructor", OffsetDateTime.now(), OffsetDateTime.now().plusDays(7));
        var enrollment = new CourseEnrollment(courseId, "Kurs", memberId, "Max Mustermann", "WAITING_LIST", "Kommentar");

        when(courseServiceStub.getById(courseId)).thenReturn(course);
        doAnswer(invocation -> {
            // validateEnrollmentCapacity ist void
            return null;
        }).when(courseServiceStub).validateEnrollmentCapacity(courseId);
        when(courseEnrollmentRepositoryStub.save(any(CourseEnrollment.class))).thenReturn(enrollment);
        doAnswer(invocation -> {
            // incrementWaitingListCount ist void
            return null;
        }).when(courseServiceStub).incrementWaitingListCount(courseId);

        // Act
        var result = sut.create(courseId, memberId, "Max Mustermann", "WAITING_LIST", "Kommentar");

        // Assert
        assertThat(result).isNotNull();
    }
}