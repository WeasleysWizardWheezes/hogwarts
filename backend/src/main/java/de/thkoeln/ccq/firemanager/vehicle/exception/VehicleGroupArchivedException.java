package de.thkoeln.ccq.firemanager.vehicle.exception;

import java.util.UUID;

public class VehicleGroupArchivedException extends RuntimeException {
    public VehicleGroupArchivedException(UUID id) {
        super("VehicleGroup mit ID " + id + " ist archiviert und kann nicht bearbeitet werden");
    }
}