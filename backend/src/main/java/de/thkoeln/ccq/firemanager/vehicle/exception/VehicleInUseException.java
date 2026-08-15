package de.thkoeln.ccq.firemanager.vehicle.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class VehicleInUseException extends ResourceConflictException {
    public VehicleInUseException() {
        super("Vehicle is currently in use and cannot be archived");
    }
}
