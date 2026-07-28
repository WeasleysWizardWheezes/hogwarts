package de.thkoeln.ccq.firemanager.vehicle.exception;

import java.util.UUID;

public class VehicleArchivedException extends RuntimeException {
    public VehicleArchivedException(UUID id) {
        super("Vehicle mit ID " + id + " ist archiviert und kann nicht bearbeitet werden");
    }
}