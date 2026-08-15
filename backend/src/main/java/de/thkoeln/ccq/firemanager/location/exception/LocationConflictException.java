package de.thkoeln.ccq.firemanager.location.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class LocationConflictException extends ResourceConflictException {
    public LocationConflictException(String message) {
        super(message);
    }
}
