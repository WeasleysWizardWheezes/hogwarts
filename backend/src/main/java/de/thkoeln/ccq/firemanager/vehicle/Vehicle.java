package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.group.VehicleGroup;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String funkrufname;

    @Column(nullable = false)
    private String kennzeichen;

    @Column
    private Integer baujahr;

    @Column
    private String beschreibung;

    @Column(nullable = false, updatable = false)
    private Instant erstellzeitpunkt;

    @Column(nullable = false)
    private Instant updatezeitpunkt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Column(nullable = false)
    private boolean isArchived;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_group_id", nullable = false)
    private VehicleGroup vehicleGroup;

    @PrePersist
    protected void onCreate() {
        erstellzeitpunkt = Instant.now();
        updatezeitpunkt = Instant.now();
        isArchived = false;
        status = VehicleStatus.VERFUEGBAR;
    }

    @PreUpdate
    protected void onUpdate() {
        updatezeitpunkt = Instant.now();
    }
}