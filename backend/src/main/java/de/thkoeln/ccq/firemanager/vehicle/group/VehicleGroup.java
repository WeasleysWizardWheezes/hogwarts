package de.thkoeln.ccq.firemanager.vehicle.group;

import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
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

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    private Instant erstellzeitpunkt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatezeitpunkt;

    @Column(nullable = false)
    private boolean isArchived = false;

    @OneToMany(mappedBy = "vehicleGroup", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles;
}