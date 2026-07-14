package de.thkoeln.ccq.firemanager.equipment.application;

public class EquipmentAlreadyExistsException extends RuntimeException {
    private static final String ERROR_CODE = "EQUIPMENT_ALREADY_EXISTS";

    public EquipmentAlreadyExistsException(String serialNumber) {
        super("Equipment mit Seriennummer %s existiert bereits".formatted(serialNumber));
    }

    public String getErrorCode() {
        return ERROR_CODE;
    }
}