package de.thkoeln.ccq.vehicle.exception;

import java.util.UUID;

public class VehicleGroupNotFoundException extends RuntimeException {

    public VehicleGroupNotFoundException(UUID id) {
        super("Vehicle group with id " + id + " not found");
    }
}
