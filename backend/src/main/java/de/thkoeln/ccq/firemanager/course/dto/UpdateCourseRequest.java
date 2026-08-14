package de.thkoeln.ccq.firemanager.course.dto;

import de.thkoeln.ccq.firemanager.course.Course;

import java.util.List;
import java.util.UUID;

public record UpdateCourseRequest(
        String name,
        String description,
        Course.CourseCategory category,
        List<UUID> prerequisites
) {
}
