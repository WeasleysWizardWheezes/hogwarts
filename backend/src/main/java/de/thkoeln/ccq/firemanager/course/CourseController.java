package de.thkoeln.ccq.firemanager.course;

import de.thkoeln.ccq.firemanager.course.enrollment.CourseEnrollmentService;
import de.thkoeln.ccq.firemanager.course.enrollment.CourseEnrollment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;
    private final CourseEnrollmentService courseEnrollmentService;

    public CourseController(
            CourseService courseService,
            CourseEnrollmentService courseEnrollmentService
    ) {
        this.courseService = courseService;
        this.courseEnrollmentService = courseEnrollmentService;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(
            @RequestBody CreateCourseRequest request
    ) {
        Course course;
        if (request.status() == null) {
            course = courseService.createWithDefaults(
                    request.name(),
                    request.description(),
                    request.maxParticipants(),
                    request.instructorId(),
                    request.instructorName(),
                    request.startDate(),
                    request.endDate()
            );
        } else {
            course = courseService.create(
                    request.name(),
                    request.description(),
                    request.maxParticipants(),
                    request.instructorId(),
                    request.instructorName(),
                    request.startDate(),
                    request.endDate(),
                    request.status()
            );
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(course);
    }

    @GetMapping
    public ResponseEntity<List<Course>> getAll() {
        var courses = courseService.getAll();
        return ResponseEntity.status(HttpStatus.OK).body(courses);
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourseById(
            @PathVariable UUID courseId
    ) {
        Course course = this.courseService.getById(courseId);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable UUID courseId,
            @RequestBody UpdateCourseRequest request
    ) {
        var course = courseService.update(
                courseId,
                request.name(),
                request.description(),
                request.maxParticipants(),
                request.instructorId(),
                request.instructorName(),
                request.startDate(),
                request.endDate(),
                request.status()
        );
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable UUID courseId
    ) {
        this.courseService.deleteById(courseId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{courseId}/enrollments")
    public ResponseEntity<CourseEnrollment> createEnrollment(
            @PathVariable UUID courseId,
            @RequestBody CreateEnrollmentRequest request
    ) {
        var enrollment = courseEnrollmentService.create(
                courseId,
                request.memberId(),
                request.memberName(),
                request.status(),
                request.comment()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(enrollment);
    }

    @GetMapping("/{courseId}/enrollments")
    public ResponseEntity<List<CourseEnrollment>> getEnrollmentsByCourse(
            @PathVariable UUID courseId
    ) {
        var enrollments = courseEnrollmentService.getAllByCourse(courseId);
        return ResponseEntity.ok(enrollments);
    }

    @DeleteMapping("/{courseId}/enrollments/{enrollmentId}")
    public ResponseEntity<Void> cancelEnrollment(
            @PathVariable UUID courseId,
            @PathVariable UUID enrollmentId
    ) {
        this.courseEnrollmentService.cancelEnrollment(enrollmentId);
        return ResponseEntity.noContent().build();
    }

    public record CreateCourseRequest(
            String name,
            String description,
            int maxParticipants,
            UUID instructorId,
            String instructorName,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            String status
    ) {
        public CreateCourseRequest {
            if (name == null) {
                throw new IllegalArgumentException("name must not be null");
            }
            if (maxParticipants < 1) {
                throw new IllegalArgumentException("maxParticipants must be positive");
            }
            if (instructorId == null) {
                throw new IllegalArgumentException("instructorId must not be null");
            }
            if (startDate == null) {
                throw new IllegalArgumentException("startDate must not be null");
            }
            if (endDate == null) {
                throw new IllegalArgumentException("endDate must not be null");
            }
        }
    }

    public record UpdateCourseRequest(
            String name,
            String description,
            int maxParticipants,
            UUID instructorId,
            String instructorName,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            String status
    ) {}

    public record CreateEnrollmentRequest(
            UUID memberId,
            String memberName,
            String status,
            String comment
    ) {}
}