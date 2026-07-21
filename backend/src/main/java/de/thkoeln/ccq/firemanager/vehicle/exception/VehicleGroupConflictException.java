package de.thkoeln.ccq.firemanager.vehicle.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class VehicleGroupConflictException extends ResourceConflictException {
    public VehicleGroupConflictException(String message) {
        super(message);
    }
}