package de.thkoeln.ccq.firemanager.course.enrollment;

import de.thkoeln.ccq.firemanager.course.Course;
import de.thkoeln.ccq.firemanager.course.CourseService;
import de.thkoeln.ccq.firemanager.course.enrollment.exception.CourseEnrollmentConflictException;
import de.thkoeln.ccq.firemanager.course.enrollment.exception.CourseEnrollmentNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CourseEnrollmentService {

    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final CourseService courseService;

    public CourseEnrollmentService(
            CourseEnrollmentRepository courseEnrollmentRepository,
            CourseService courseService
    ) {
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.courseService = courseService;
    }

    public CourseEnrollment create(
            UUID courseId,
            UUID memberId,
            String memberName,
            String status,
            String comment,
            OffsetDateTime enrolledAt,
            OffsetDateTime confirmedAt
    ) {
        // Validate course capacity
        courseService.validateEnrollmentCapacity(courseId);
        
        Course course = courseService.getById(courseId);
        
        CourseEnrollment enrollment = new CourseEnrollment(
                courseId,
                course.getName(),
                memberId,
                memberName,
                status,
                comment,
                enrolledAt,
                confirmedAt
        );
        
        CourseEnrollment savedEnrollment = this.courseEnrollmentRepository.save(enrollment);
        
        // Update course counters based on status
        if ("CONFIRMED".equals(status)) {
            courseService.incrementCurrentParticipants(courseId);
        } else if ("WAITING_LIST".equals(status)) {
            courseService.incrementWaitingListCount(courseId);
        }
        
        return savedEnrollment;
    }

    public CourseEnrollment create(UUID courseId, UUID memberId, String memberName, String status, String comment) {
        return this.create(courseId, memberId, memberName, status, comment, OffsetDateTime.now(), null);
    }

    public CourseEnrollment create(UUID courseId, UUID memberId, String memberName, String status) {
        return this.create(courseId, memberId, memberName, status, null);
    }

    public List<CourseEnrollment> getAllByCourse(UUID courseId) {
        return this.courseEnrollmentRepository.findByCourseId(courseId);
    }

    public List<CourseEnrollment> getAllByMember(UUID memberId) {
        return this.courseEnrollmentRepository.findByMemberId(memberId);
    }

    public CourseEnrollment getById(UUID enrollmentId) {
        if (enrollmentId == null) {
            throw new IllegalArgumentException("enrollmentId must not be null");
        }
        return courseEnrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new CourseEnrollmentNotFoundException(enrollmentId));
    }

    public void cancelEnrollment(UUID enrollmentId) {
        CourseEnrollment enrollment = getById(enrollmentId);
        
        // Update course counters based on previous status
        if ("CONFIRMED".equals(enrollment.getStatus())) {
            courseService.decrementCurrentParticipants(enrollment.getCourseId());
        } else if ("WAITING_LIST".equals(enrollment.getStatus())) {
            courseService.decrementWaitingListCount(enrollment.getCourseId());
        }
        
        enrollment.setStatus("CANCELLED");
        enrollment.setUpdatedAt(OffsetDateTime.now());
        this.courseEnrollmentRepository.save(enrollment);
    }

    public void deleteById(UUID enrollmentId) {
        CourseEnrollment enrollment = getById(enrollmentId);
        
        // Update course counters based on status
        if ("CONFIRMED".equals(enrollment.getStatus())) {
            courseService.decrementCurrentParticipants(enrollment.getCourseId());
        } else if ("WAITING_LIST".equals(enrollment.getStatus())) {
            courseService.decrementWaitingListCount(enrollment.getCourseId());
        }
        
        courseEnrollmentRepository.deleteById(enrollmentId);
    }
}