package de.thkoeln.ccq.firemanager.course.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceNotFoundException;

import java.util.UUID;

public class CourseNotFoundException extends ResourceNotFoundException {
    public CourseNotFoundException(UUID id) {
        super("Course", id);
    }
}
