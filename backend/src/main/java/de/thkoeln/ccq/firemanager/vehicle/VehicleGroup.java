package de.thkoeln.ccq.firemanager.vehicle;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
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

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;

    protected VehicleGroup() {
        // JPA only
    }

    public VehicleGroup(String name, String description) {
        Assert.hasText(name, "name must not be empty");
        
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public VehicleGroup(String name) {
        this(name, null);
    }

    public void update(String name, String description) {
        Assert.hasText(name, "name must not be empty");
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
}