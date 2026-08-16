package de.thkoeln.ccq.firemanager.course.enrollment.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceNotFoundException;

import java.util.UUID;

public class CourseEnrollmentNotFoundException extends ResourceNotFoundException {
    public CourseEnrollmentNotFoundException(UUID id) {
        super("CourseEnrollment", id);
    }
}