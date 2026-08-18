package de.thkoeln.ccq.firemanager.equipmentcategory.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceConflictException;

public class EquipmentCategoryConflictException extends ResourceConflictException {

    public EquipmentCategoryConflictException(String message) {
        super(message);
    }
}
