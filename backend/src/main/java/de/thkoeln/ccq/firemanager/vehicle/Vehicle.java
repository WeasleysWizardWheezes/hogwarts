package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.group.VehicleGroup;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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