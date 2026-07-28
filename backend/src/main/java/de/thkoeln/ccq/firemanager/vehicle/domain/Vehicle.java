package de.thkoeln.ccq.firemanager.vehicle.domain;

import de.thkoeln.ccq.firemanager.vehicle.group.domain.VehicleGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(name = "funkrufname", nullable = false)
    private String funkrufname;

    @Column(name = "kennzeichen", nullable = false)
    private String kennzeichen;

    @Column(name = "baujahr")
    private Integer baujahr;

    @Column(columnDefinition = "TEXT")
    private String beschreibung;

    @Column(name = "erstellzeitpunkt", nullable = false)
    private Instant erstellzeitpunkt;

    @Column(name = "updatezeitpunkt", nullable = false)
    private Instant updatezeitpunkt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Column(name = "is_archived", nullable = false)
    @Builder.Default
    private boolean archived = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicle_group_id", nullable = false)
    private VehicleGroup vehicleGroup;

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}