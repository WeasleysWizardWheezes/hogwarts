package de.thkoeln.ccq.firemanager.errorhandling.exception;

public class ResourceConflictException extends RuntimeException {
    public ResourceConflictException(String message) {
        super(message);
    }
}