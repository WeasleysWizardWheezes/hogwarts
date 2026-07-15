package de.thkoeln.ccq.vehicle.exception;

public class VehicleLicensePlateAlreadyExistsException extends RuntimeException {

    public VehicleLicensePlateAlreadyExistsException(String kennzeichen) {
        super("Vehicle with license plate " + kennzeichen + " already exists");
    }
}
