package de.thkoeln.ccq.firemanager.vehicle;

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

import java.time.LocalDateTime;
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
    private String radioCallName;

    @Column
    @Setter
    private String licensePlate;

    @Column
    @Setter
    private Integer year;

    @Column
    @Setter
    private String description;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Setter
    private VehicleStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "vehicle_group_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter
    private VehicleGroup vehicleGroup;

    protected Vehicle() {
        // JPA only
    }

    public Vehicle(String name, String radioCallName, String licensePlate, 
                   Integer year, String description, VehicleStatus status, 
                   VehicleGroup vehicleGroup) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(radioCallName, "radioCallName must not be empty");
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (vehicleGroup == null) {
            throw new IllegalArgumentException("vehicleGroup must not be null");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.radioCallName = radioCallName;
        this.licensePlate = licensePlate;
        this.year = year;
        this.description = description;
        this.status = status;
        this.vehicleGroup = vehicleGroup;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void update(String name, String radioCallName, String licensePlate, 
                      Integer year, String description, VehicleStatus status) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(radioCallName, "radioCallName must not be empty");
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        this.name = name;
        this.radioCallName = radioCallName;
        this.licensePlate = licensePlate;
        this.year = year;
        this.description = description;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    public enum VehicleStatus {
        AVAILABLE,
        IN_USE,
        MAINTENANCE,
        DEFECT
    }
}