package de.thkoeln.ccq.firemanager.course.enrollment.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class CourseEnrollmentConflictException extends ResourceConflictException {
    public CourseEnrollmentConflictException(String message) {
        super(message);
    }
}