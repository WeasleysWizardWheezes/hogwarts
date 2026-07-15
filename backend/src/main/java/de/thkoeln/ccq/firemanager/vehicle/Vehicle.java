package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vehicles")
@Getter
public class Vehicle {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false)
    @Setter
    private String funkrufname;

    @Column(nullable = false)
    @Setter
    private String kennzeichen;

    @Column(nullable = false)
    @Setter
    private Integer baujahr;

    @Column
    @Setter
    private String beschreibung;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private Instant erstellzeitpunkt;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private Instant updatezeitpunkt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private VehicleStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter
    private VehicleGroup gruppe;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private boolean archiviert;

    protected Vehicle() {
        // JPA only
    }

    public Vehicle(
            String name,
            String funkrufname,
            String kennzeichen,
            Integer baujahr,
            String beschreibung,
            VehicleStatus status,
            VehicleGroup gruppe
    ) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(funkrufname, "funkrufname must not be empty");
        Assert.hasText(kennzeichen, "kennzeichen must not be empty");
        if (baujahr == null || baujahr < 1900 || baujahr > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("baujahr must be valid");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (gruppe == null) {
            throw new IllegalArgumentException("gruppe must not be null");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.funkrufname = funkrufname;
        this.kennzeichen = kennzeichen;
        this.baujahr = baujahr;
        this.beschreibung = beschreibung;
        this.status = status;
        this.gruppe = gruppe;
        this.erstellzeitpunkt = Instant.now();
        this.updatezeitpunkt = Instant.now();
        this.archiviert = false;
    }

    public void update(
            String name,
            String funkrufname,
            String kennzeichen,
            Integer baujahr,
            String beschreibung,
            VehicleStatus status,
            VehicleGroup gruppe
    ) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(funkrufname, "funkrufname must not be empty");
        Assert.hasText(kennzeichen, "kennzeichen must not be empty");
        if (baujahr == null || baujahr < 1900 || baujahr > java.time.Year.now().getValue()) {
            throw new IllegalArgumentException("baujahr must be valid");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (gruppe == null) {
            throw new IllegalArgumentException("gruppe must not be null");
        }

        this.name = name;
        this.funkrufname = funkrufname;
        this.kennzeichen = kennzeichen;
        this.baujahr = baujahr;
        this.beschreibung = beschreibung;
        this.status = status;
        this.gruppe = gruppe;
        this.updatezeitpunkt = Instant.now();
    }

    public void archive() {
        this.archiviert = true;
        this.updatezeitpunkt = Instant.now();
    }

    public boolean isArchiviert() {
        return archiviert;
    }
}