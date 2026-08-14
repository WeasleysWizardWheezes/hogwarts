package de.thkoeln.ccq.firemanager.course;

import de.thkoeln.ccq.firemanager.course.dto.CreateCourseRequest;
import de.thkoeln.ccq.firemanager.course.dto.UpdateCourseRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<Course> createCourse(
            @Valid @RequestBody CreateCourseRequest request
    ) {
        var course = courseService.create(
                request.name(),
                request.description(),
                request.category(),
                request.prerequisites()
        );
        return ResponseEntity.created(URI.create("/api/v1/courses/" + course.getId())).body(course);
    }

    @GetMapping
    public ResponseEntity<List<Course>> listCourses(
            @RequestParam(required = false) Course.CourseCategory category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (category != null) {
            var courses = courseService.getByCategory(category);
            return ResponseEntity.ok(courses);
        } else {
            var courses = courseService.getAll();
            return ResponseEntity.ok(courses);
        }
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<Course> getCourse(
            @PathVariable UUID courseId
    ) {
        Course course = courseService.getById(courseId);
        return ResponseEntity.ok(course);
    }

    @PutMapping("/{courseId}")
    public ResponseEntity<Course> updateCourse(
            @PathVariable UUID courseId,
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        var course = courseService.update(
                courseId,
                request.name(),
                request.description(),
                request.category(),
                request.prerequisites()
        );
        return ResponseEntity.ok(course);
    }

    @PatchMapping("/{courseId}")
    public ResponseEntity<Course> patchCourse(
            @PathVariable UUID courseId,
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        var course = courseService.update(
                courseId,
                request.name(),
                request.description(),
                request.category(),
                request.prerequisites()
        );
        return ResponseEntity.ok(course);
    }

    @DeleteMapping("/{courseId}")
    public ResponseEntity<Void> deleteCourse(
            @PathVariable UUID courseId
    ) {
        courseService.deleteById(courseId);
        return ResponseEntity.noContent().build();
    }
}
