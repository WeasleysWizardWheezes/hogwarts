package de.thkoeln.ccq.firemanager.course.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class CourseConflictException extends ResourceConflictException {
    public CourseConflictException(String message) {
        super(message);
    }
}
