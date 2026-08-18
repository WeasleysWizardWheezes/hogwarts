package de.thkoeln.ccq.firemanager.equipment;

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

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "equipment_history")
@Getter
public class EquipmentHistory {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipment_id", nullable = false)
    private Equipment equipment;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EquipmentStatus newStatus;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime changedAt;

    protected EquipmentHistory() {
        // JPA only
    }

    public EquipmentHistory(
            Equipment equipment,
            EquipmentStatus previousStatus,
            EquipmentStatus newStatus
    ) {
        if (equipment == null) {
            throw new IllegalArgumentException("equipment must not be null");
        }
        if (newStatus == null) {
            throw new IllegalArgumentException("newStatus must not be null");
        }

        this.id = UUID.randomUUID();
        this.equipment = equipment;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.changedAt = OffsetDateTime.now();
    }
}