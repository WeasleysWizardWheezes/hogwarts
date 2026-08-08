package de.thkoeln.ccq.firemanager.vehicle.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceNotFoundException;

import java.util.UUID;

public class VehicleNotFoundException extends ResourceNotFoundException {
    public VehicleNotFoundException(UUID id) {
        super("Vehicle", id);
    }
}