package de.thkoeln.ccq.vehicle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "vehicles")
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Name is mandatory")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "funkrufname")
    private String funkrufname;

    @Column(name = "kennzeichen", unique = true)
    private String kennzeichen;

    @Column(name = "baujahr")
    private Integer baujahr;

    @Column(name = "beschreibung")
    private String beschreibung;

    @CreationTimestamp
    @Column(name = "erstellzeitpunkt", updatable = false, nullable = false)
    private Instant erstellzeitpunkt;

    @UpdateTimestamp
    @Column(name = "updatezeitpunkt", nullable = false)
    private Instant updatezeitpunkt;

    @NotNull(message = "Status is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VehicleStatus status;

    @NotNull(message = "Group is mandatory")
    @ManyToOne
    @JoinColumn(name = "gruppe_id", nullable = false)
    private VehicleGroup gruppe;

    @Builder.Default
    @Column(name = "archiviert", nullable = false)
    private boolean archiviert = false;
}
