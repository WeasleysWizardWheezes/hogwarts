package de.thkoeln.ccq.firemanager.document.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public record UploadDocumentRequest(
        @NotNull(message = "file must not be null")
        MultipartFile file,

        @NotNull(message = "memberId must not be null")
        UUID memberId
) {
}