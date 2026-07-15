package de.thkoeln.ccq.firemanager.vehicle.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class VehicleConflictException extends ResourceConflictException {
    public VehicleConflictException(String message) {
        super(message);
    }
}