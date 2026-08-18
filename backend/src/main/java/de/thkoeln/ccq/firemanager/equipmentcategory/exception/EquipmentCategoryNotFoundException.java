package de.thkoeln.ccq.firemanager.equipmentcategory.exception;

import de.thkoeln.ccq.firemanager.errorhandling.exception.ResourceNotFoundException;

import java.util.UUID;

public class EquipmentCategoryNotFoundException extends ResourceNotFoundException {

    public EquipmentCategoryNotFoundException(UUID id) {
        super("EquipmentCategory", id);
    }
}
