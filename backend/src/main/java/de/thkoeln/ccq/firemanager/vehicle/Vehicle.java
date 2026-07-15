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

    @Column
    @Setter
    private String funkrufname;

    @Column
    @Setter
    private String kennzeichen;

    @Column
    @Setter
    private Integer baujahr;

    @Column
    @Setter
    private String beschreibung;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private Instant erstellzeitpunkt;

    @Column(nullable = false)
    @Setter
    private Instant updatezeitpunkt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private VehicleStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_group_id", nullable = false)
    @Setter
    private VehicleGroup gruppe;

    @Column(nullable = false)
    @Setter
    private boolean archiviert;

    protected Vehicle() {
        // JPA only
    }

    public Vehicle(String name, String funkrufname, String kennzeichen, Integer baujahr, 
                   String beschreibung, VehicleStatus status, VehicleGroup gruppe) {
        Assert.hasText(name, "name must not be empty");
        Assert.notNull(status, "status must not be null");
        Assert.notNull(gruppe, "gruppe must not be null");
        
        this.id = UUID.randomUUID();
        this.name = name;
        this.funkrufname = funkrufname;
        this.kennzeichen = kennzeichen;
        this.baujahr = baujahr;
        this.beschreibung = beschreibung;
        this.status = status;
        this.gruppe = gruppe;
        this.archiviert = false;
        this.erstellzeitpunkt = Instant.now();
        this.updatezeitpunkt = Instant.now();
    }

    public Vehicle(String name, String funkrufname, String kennzeichen, Integer baujahr, 
                   String beschreibung, VehicleStatus status) {
        this(name, funkrufname, kennzeichen, baujahr, beschreibung, status, null);
    }
}