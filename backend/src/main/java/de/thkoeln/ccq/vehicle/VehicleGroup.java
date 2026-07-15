package de.thkoeln.ccq.vehicle;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehicle_groups")
public class VehicleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "beschreibung")
    private String beschreibung;

    @CreationTimestamp
    @Column(name = "erstellzeitpunkt", updatable = false, nullable = false)
    private Instant erstellzeitpunkt;

    @UpdateTimestamp
    @Column(name = "updatezeitpunkt", nullable = false)
    private Instant updatezeitpunkt;

    @Builder.Default
    @Column(name = "archiviert", nullable = false)
    private boolean archiviert = false;
}
