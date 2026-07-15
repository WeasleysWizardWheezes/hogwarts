package de.thkoeln.ccq.firemanager.vehiclegroup.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceNotFoundException;

import java.util.UUID;

public class VehicleGroupNotFoundException extends ResourceNotFoundException {
    public VehicleGroupNotFoundException(UUID id) {
        super("VehicleGroup", id);
    }
}