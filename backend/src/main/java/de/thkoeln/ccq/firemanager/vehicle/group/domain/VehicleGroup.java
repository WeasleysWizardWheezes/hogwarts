package de.thkoeln.ccq.firemanager.vehicle.group.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "vehicle_groups")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String beschreibung;

    @Column(name = "erstellzeitpunkt", nullable = false)
    private Instant erstellzeitpunkt;

    @Column(name = "updatezeitpunkt", nullable = false)
    private Instant updatezeitpunkt;

    @Column(name = "is_archived", nullable = false)
    @Builder.Default
    private boolean archived = false;

    public void setArchived(boolean archived) {
        this.archived = archived;
    }
}