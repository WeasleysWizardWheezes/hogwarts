package de.thkoeln.ccq.firemanager.vehicle.exception;

import java.util.UUID;

public class VehicleNotFoundException extends RuntimeException {
    public VehicleNotFoundException(UUID id) {
        super("Vehicle mit ID " + id + " nicht gefunden");
    }
}