package de.thkoeln.ccq.firemanager.vehiclegroup;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.time.OffsetDateTime;
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
    private String description;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime updatedAt;

    @Column(nullable = false)
    @Setter
    private boolean archived;

    protected VehicleGroup() {
        // JPA only
    }

    public VehicleGroup(String name, String description) {
        Assert.hasText(name, "name must not be empty");

        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
        this.archived = false;
    }

    public VehicleGroup(String name) {
        this(name, null);
    }
}
