package de.thkoeln.ccq.firemanager.equipment.api;

import java.util.List;

public record EquipmentPageResponse(
        List<EquipmentResponse> data,
        int page,
        int size,
        long totalElements,
        int totalPages
) {}