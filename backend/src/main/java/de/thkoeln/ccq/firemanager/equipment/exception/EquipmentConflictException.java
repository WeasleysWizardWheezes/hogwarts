package de.thkoeln.ccq.firemanager.equipment.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class EquipmentConflictException extends ResourceConflictException {

    public EquipmentConflictException(String message) {
        super(message);
    }
}