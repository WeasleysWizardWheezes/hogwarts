package de.thkoeln.ccq.firemanager.vehicle;

import de.thkoeln.ccq.firemanager.vehiclegroup.VehicleGroup;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
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
    private String callSign;

    @Column(nullable = false)
    @Setter
    private String licensePlate;

    @Column(nullable = false)
    @Setter
    private int yearOfConstruction;

    @Column
    @Setter
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private VehicleStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter
    private VehicleGroup vehicleGroup;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private Instant createdAt;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private Instant updatedAt;

    protected Vehicle() {
        // JPA only
    }

    public Vehicle(String name, String callSign, String licensePlate, 
            int yearOfConstruction, String description, VehicleStatus status, 
            VehicleGroup vehicleGroup) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(callSign, "callSign must not be empty");
        Assert.hasText(licensePlate, "licensePlate must not be empty");
        if (yearOfConstruction < 1900 || yearOfConstruction > 2100) {
            throw new IllegalArgumentException("yearOfConstruction must be between 1900 and 2100");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (vehicleGroup == null) {
            throw new IllegalArgumentException("vehicleGroup must not be null");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.callSign = callSign;
        this.licensePlate = licensePlate;
        this.yearOfConstruction = yearOfConstruction;
        this.description = description;
        this.status = status;
        this.vehicleGroup = vehicleGroup;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public Vehicle(String name, String callSign, String licensePlate, 
            int yearOfConstruction, VehicleGroup vehicleGroup) {
        this(name, callSign, licensePlate, yearOfConstruction, null, VehicleStatus.AVAILABLE, vehicleGroup);
    }

    public void update(String name, String callSign, String licensePlate, 
            int yearOfConstruction, String description, VehicleStatus status, 
            VehicleGroup vehicleGroup) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(callSign, "callSign must not be empty");
        Assert.hasText(licensePlate, "licensePlate must not be empty");
        if (yearOfConstruction < 1900 || yearOfConstruction > 2100) {
            throw new IllegalArgumentException("yearOfConstruction must be between 1900 and 2100");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (vehicleGroup == null) {
            throw new IllegalArgumentException("vehicleGroup must not be null");
        }

        this.name = name;
        this.callSign = callSign;
        this.licensePlate = licensePlate;
        this.yearOfConstruction = yearOfConstruction;
        this.description = description;
        this.status = status;
        this.vehicleGroup = vehicleGroup;
        this.updatedAt = Instant.now();
    }

    public enum VehicleStatus {
        AVAILABLE,
        IN_USE,
        IN_MAINTENANCE,
        DEFECTIVE
    }
}