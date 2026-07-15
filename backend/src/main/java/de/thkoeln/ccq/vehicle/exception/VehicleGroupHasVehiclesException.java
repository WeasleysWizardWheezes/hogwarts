package de.thkoeln.ccq.vehicle.exception;

import java.util.UUID;

public class VehicleGroupHasVehiclesException extends RuntimeException {

    public VehicleGroupHasVehiclesException(UUID id) {
        super("Vehicle group with id " + id + " cannot be deleted because it still has vehicles assigned");
    }
}
