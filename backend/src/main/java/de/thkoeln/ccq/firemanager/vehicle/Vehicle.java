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

import java.time.OffsetDateTime;
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
    private String radioCallName;

    @Column
    @Setter
    private String licensePlate;

    @Column
    @Setter
    private Integer yearOfConstruction;

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

    @Column(nullable = false)
    @Setter
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime updatedAt;

    @Column(nullable = false)
    @Setter
    private boolean archived;

    protected Vehicle() {
        // JPA only
    }

    public Vehicle(
            String name,
            String radioCallName,
            String licensePlate,
            Integer yearOfConstruction,
            String description,
            VehicleStatus status,
            VehicleGroup vehicleGroup
    ) {
        Assert.hasText(name, "name must not be empty");
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
        this.yearOfConstruction = yearOfConstruction;
        this.description = description;
        this.status = status;
        this.vehicleGroup = vehicleGroup;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.archived = false;
    }

    public Vehicle(String name, VehicleStatus status, VehicleGroup vehicleGroup) {
        this(name, null, null, null, null, status, vehicleGroup);
    }

    public enum VehicleStatus {
        VERFUEGBAR,
        IM_EINSATZ,
        WARTUNG,
        DEFEKT
    }
}
