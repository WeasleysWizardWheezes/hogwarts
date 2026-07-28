package de.thkoeln.ccq.firemanager.vehicle.group;

import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vehicle_groups")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String beschreibung;

    @Column(nullable = false, updatable = false)
    private Instant erstellzeitpunkt;

    @Column(nullable = false)
    private Instant updatezeitpunkt;

    @Column(nullable = false)
    private boolean isArchived;

    @OneToMany(mappedBy = "vehicleGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        erstellzeitpunkt = Instant.now();
        updatezeitpunkt = Instant.now();
        isArchived = false;
    }

    @PreUpdate
    protected void onUpdate() {
        updatezeitpunkt = Instant.now();
    }
}