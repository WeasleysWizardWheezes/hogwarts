package de.thkoeln.ccq.firemanager.course.enrollment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, UUID> {

    List<CourseEnrollment> findByCourseId(UUID courseId);

    List<CourseEnrollment> findByMemberId(UUID memberId);
}