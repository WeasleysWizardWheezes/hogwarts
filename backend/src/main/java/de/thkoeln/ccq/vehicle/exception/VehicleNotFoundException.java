package de.thkoeln.ccq.vehicle.exception;

import java.util.UUID;

public class VehicleNotFoundException extends RuntimeException {

    public VehicleNotFoundException(UUID id) {
        super("Vehicle with id " + id + " not found");
    }
}
