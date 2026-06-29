package de.thkoeln.ccq.firemanager.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleDTO {
    private String id;
    private String name;
    private String description;
    private String conditions;
    private String actions;
}
