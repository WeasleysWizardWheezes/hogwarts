package de.thkoeln.ccq.firemanager.vehicle.exception;

import java.util.UUID;

public class VehicleGroupNotFoundException extends RuntimeException {
    public VehicleGroupNotFoundException(UUID id) {
        super("VehicleGroup mit ID " + id + " nicht gefunden");
    }
}