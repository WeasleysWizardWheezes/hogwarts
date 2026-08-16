package de.thkoeln.ccq.firemanager.course;

import de.thkoeln.ccq.firemanager.course.exception.CourseConflictException;
import de.thkoeln.ccq.firemanager.course.exception.CourseNotFoundException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course create(
            String name,
            String description,
            int maxParticipants,
            UUID instructorId,
            String instructorName,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            String status
    ) {
        Course course = new Course(
                name,
                description,
                maxParticipants,
                instructorId,
                instructorName,
                startDate,
                endDate,
                status
        );
        return this.courseRepository.save(course);
    }

    public Course createWithDefaults(
            String name,
            String description,
            int maxParticipants,
            UUID instructorId,
            String instructorName,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    ) {
        return this.create(
                name,
                description,
                maxParticipants,
                instructorId,
                instructorName,
                startDate,
                endDate,
                "PLANNED"
        );
    }

    public Course create(
            String name,
            String description,
            int maxParticipants,
            UUID instructorId,
            String instructorName,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    ) {
        return this.create(
                name,
                description,
                maxParticipants,
                instructorId,
                instructorName,
                startDate,
                endDate,
                "PLANNED"
        );
    }

    public List<Course> getAll() {
        return this.courseRepository.findAll();
    }

    public Course getById(UUID courseId) {
        if (courseId == null) {
            throw new IllegalArgumentException("courseId must not be null");
        }
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new CourseNotFoundException(courseId));
    }

    public Course update(
            UUID courseId,
            String name,
            String description,
            int maxParticipants,
            UUID instructorId,
            String instructorName,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            String status
    ) {
        Course existingCourse = getById(courseId);
        existingCourse.setName(name);
        existingCourse.setDescription(description);
        existingCourse.setMaxParticipants(maxParticipants);
        existingCourse.setInstructorId(instructorId);
        existingCourse.setInstructorName(instructorName);
        existingCourse.setStartDate(startDate);
        existingCourse.setEndDate(endDate);
        existingCourse.setStatus(status);
        existingCourse.setUpdatedAt(OffsetDateTime.now());
        return this.courseRepository.save(existingCourse);
    }

    public void deleteById(UUID courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(courseId);
        }
        courseRepository.deleteById(courseId);
    }

    public void validateEnrollmentCapacity(UUID courseId) {
        Course course = getById(courseId);
        if (course.getCurrentParticipants() >= course.getMaxParticipants()) {
            throw new CourseConflictException("Course is full. Maximum participants reached.");
        }
    }

    public void incrementCurrentParticipants(UUID courseId) {
        Course course = getById(courseId);
        course.incrementCurrentParticipants();
        courseRepository.save(course);
    }

    public void incrementWaitingListCount(UUID courseId) {
        Course course = getById(courseId);
        course.incrementWaitingListCount();
        courseRepository.save(course);
    }

    public void decrementCurrentParticipants(UUID courseId) {
        Course course = getById(courseId);
        course.decrementCurrentParticipants();
        courseRepository.save(course);
    }

    public void decrementWaitingListCount(UUID courseId) {
        Course course = getById(courseId);
        course.decrementWaitingListCount();
        courseRepository.save(course);
    }
}