package de.thkoeln.ccq.firemanager.vehiclegroup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vehicle_groups")
@Getter
public class VehicleGroup {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column
    @Setter
    private String beschreibung;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private Instant erstellzeitpunkt;

    @Column(nullable = false)
    @Setter
    private Instant updatezeitpunkt;

    @Column(nullable = false)
    @Setter
    private boolean archiviert;

    protected VehicleGroup() {
        // JPA only
    }

    public VehicleGroup(String name, String beschreibung) {
        Assert.hasText(name, "name must not be empty");
        
        this.id = UUID.randomUUID();
        this.name = name;
        this.beschreibung = beschreibung;
        this.archiviert = false;
        this.erstellzeitpunkt = Instant.now();
        this.updatezeitpunkt = Instant.now();
    }

    public VehicleGroup(String name) {
        this(name, null);
    }
}