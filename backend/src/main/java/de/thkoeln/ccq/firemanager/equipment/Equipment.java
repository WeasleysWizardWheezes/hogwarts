package de.thkoeln.ccq.firemanager.equipment;

import de.thkoeln.ccq.firemanager.equipmentcategory.EquipmentCategory;
import de.thkoeln.ccq.firemanager.vehicle.Vehicle;
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

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "equipment")
@Getter
public class Equipment {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false, unique = true)
    @Setter
    private String inventoryNumber;

    @Setter
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private EquipmentStatus status;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @Setter
    private EquipmentCategory category;

    @ManyToOne
    @JoinColumn(name = "vehicle_id")
    @Setter
    private Vehicle vehicle;

    @Setter
    private LocalDate nextInspectionDate;

    @Setter
    private LocalDate nextMaintenanceDate;

    @Column(nullable = false)
    @Setter
    private boolean archived;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime updatedAt;

    protected Equipment() {
        // JPA only
    }

    public Equipment(
            String name,
            String inventoryNumber,
            String description,
            EquipmentStatus status,
            EquipmentCategory category,
            Vehicle vehicle,
            LocalDate nextInspectionDate,
            LocalDate nextMaintenanceDate
    ) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(inventoryNumber, "inventoryNumber must not be empty");
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.inventoryNumber = inventoryNumber;
        this.description = description;
        this.status = status;
        this.category = category;
        this.vehicle = vehicle;
        this.nextInspectionDate = nextInspectionDate;
        this.nextMaintenanceDate = nextMaintenanceDate;
        this.archived = false;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }
}