package de.thkoeln.ccq.firemanager.equipment.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceNotFoundException;

import java.util.UUID;

public class EquipmentNotFoundException extends ResourceNotFoundException {

    public EquipmentNotFoundException(UUID id) {
        super("Equipment", id);
    }
}