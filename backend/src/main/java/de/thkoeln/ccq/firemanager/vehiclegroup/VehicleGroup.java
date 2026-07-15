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

    @Column(nullable = false)
    @Setter
    private String beschreibung;

    @Column(nullable = false)
    @Setter
    private Instant erstellzeitpunkt;

    @Column(nullable = false)
    @Setter
    private Instant updatezeitpunkt;

    protected VehicleGroup() {
        // JPA only
    }

    public VehicleGroup(String name, String beschreibung) {
        Assert.hasText(name, "name must not be empty");
        if (beschreibung == null) {
            throw new IllegalArgumentException("beschreibung must not be null");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.beschreibung = beschreibung;
        this.erstellzeitpunkt = Instant.now();
        this.updatezeitpunkt = Instant.now();
    }
}
