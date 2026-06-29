package de.thkoeln.ccq.firemanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String title;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean isRecurring;
    private String recurrencePattern;

    @ManyToOne
    @JoinColumn(name = "organization_unit_id")
    private OrganizationUnit organizationUnit;
}
