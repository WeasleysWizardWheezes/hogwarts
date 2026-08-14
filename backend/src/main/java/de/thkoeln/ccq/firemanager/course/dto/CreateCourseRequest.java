package de.thkoeln.ccq.firemanager.course.dto;

import de.thkoeln.ccq.firemanager.course.Course;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateCourseRequest(
        @NotBlank(message = "name must not be empty")
        String name,

        @NotBlank(message = "description must not be empty")
        String description,

        @NotNull
        Course.CourseCategory category,

        List<UUID> prerequisites
) {
}
