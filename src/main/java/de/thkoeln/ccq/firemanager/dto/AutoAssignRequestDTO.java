package de.thkoeln.ccq.firemanager.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AutoAssignRequestDTO {
    private String appointmentId;
    private String ruleId;
}
