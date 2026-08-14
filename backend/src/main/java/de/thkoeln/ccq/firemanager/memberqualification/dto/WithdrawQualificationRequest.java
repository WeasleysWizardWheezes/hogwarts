package de.thkoeln.ccq.firemanager.memberqualification.dto;

import jakarta.validation.constraints.NotBlank;

public record WithdrawQualificationRequest(
        @NotBlank(message = "reason must not be empty")
        String reason
) {
}
