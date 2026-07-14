package de.thkoeln.ccq.firemanager.equipment.application;

import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentId;

public class EquipmentNotFoundException extends RuntimeException {
    private static final String ERROR_CODE = "EQUIPMENT_NOT_FOUND";

    public EquipmentNotFoundException(EquipmentId id) {
        super("Equipment mit ID %s nicht gefunden".formatted(id.value()));
    }

    public String getErrorCode() {
        return ERROR_CODE;
    }
}