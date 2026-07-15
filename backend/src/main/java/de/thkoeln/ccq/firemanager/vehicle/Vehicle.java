package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehicle.group.VehicleGroup;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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

    @Column(nullable = false)
    private Integer baujahr;

    @Column
    private String beschreibung;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant erstellzeitpunkt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatezeitpunkt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status = VehicleStatus.VERFUEGBAR;

    @Column(nullable = false)
    private boolean isArchived = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_group_id", nullable = false)
    private VehicleGroup vehicleGroup;
}