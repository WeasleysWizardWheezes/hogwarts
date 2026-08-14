package de.thkoeln.ccq.firemanager.document.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record DocumentResponse(
        UUID id,
        String originalName,
        String mimeType,
        Long size,
        UUID uploadedBy,
        LocalDateTime uploadedAt,
        String downloadUrl
) {
}