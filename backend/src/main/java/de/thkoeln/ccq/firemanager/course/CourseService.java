package de.thkoeln.ccq.firemanager.course;

import de.thkoeln.ccq.firemanager.course.exception.CourseConflictException;
import de.thkoeln.ccq.firemanager.course.exception.CourseNotFoundException;
import de.thkoeln.ccq.firemanager.memberqualification.MemberQualificationService;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final MemberQualificationService memberQualificationService;
    private final Clock clock;

    public CourseService(
            CourseRepository courseRepository,
            MemberQualificationService memberQualificationService,
            Clock clock
    ) {
        this.courseRepository = courseRepository;
        this.memberQualificationService = memberQualificationService;
        this.clock = clock;
    }

    public Course create(String name, String description, Course.CourseCategory category, List<UUID> prerequisites) {
        if (courseRepository.existsByName(name)) {
            throw new CourseConflictException("Course with name " + name + " already exists");
        }

        Course course = new Course(name, description, category, prerequisites);
        return courseRepository.save(course);
    }

    public Course create(String name, String description, Course.CourseCategory category) {
        return this.create(name, description, category, List.of());
    }

    public List<Course> getAll() {
        return courseRepository.findAll();
    }

    public List<Course> getByCategory(Course.CourseCategory category) {
        return courseRepository.findByCategory(category);
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
            Course.CourseCategory category,
            List<UUID> prerequisites
    ) {
        Course existingCourse = getById(courseId);
        
        boolean nameChanged = !existingCourse.getName().equals(name);
        boolean nameExists = courseRepository.existsByName(name);
        
        if (nameChanged && nameExists) {
            throw new CourseConflictException(
                    "Course with name " + name + " already exists"
            );
        }

        existingCourse.update(name, description, category, prerequisites);
        return courseRepository.save(existingCourse);
    }

    public void deleteById(UUID courseId) {
        Course course = getById(courseId);
        
        // Prüfen ob die Qualifikation noch zugewiesen ist
        if (memberQualificationService.existsByCourseId(courseId)) {
            throw new CourseConflictException("Course cannot be deleted because it is still assigned to members");
        }
        
        courseRepository.deleteById(courseId);
    }

    public List<Course> getPrerequisites(List<UUID> prerequisiteIds) {
        return courseRepository.findPrerequisites(prerequisiteIds);
    }

    public boolean hasMemberAllPrerequisites(UUID memberId, UUID courseId) {
        Course course = getById(courseId);
        List<UUID> prerequisiteIds = course.getPrerequisites();
        
        if (prerequisiteIds.isEmpty()) {
            return true;
        }
        
        return memberQualificationService.hasMemberAllQualifications(memberId, prerequisiteIds);
    }
}
