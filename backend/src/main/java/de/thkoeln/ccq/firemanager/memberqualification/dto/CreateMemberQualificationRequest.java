package de.thkoeln.ccq.firemanager.memberqualification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record CreateMemberQualificationRequest(
        @NotNull
        UUID courseId,

        @NotNull
        LocalDate acquisitionDate,

        String certificateNumber,

        String issuingAuthority
) {
}
