package de.thkoeln.ccq.firemanager.location;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.util.UUID;

@Entity
@Table(name = "locations")
@Getter
public class Location {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = true)
    @Setter
    private String address;

    @Column(nullable = false)
    @Setter
    private String type;

    protected Location() {
        // JPA only
    }

    public Location(String name, String address, String type) {
        Assert.hasText(name, "name must not be empty");
        if (type == null) {
            throw new IllegalArgumentException("type must not be null");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.type = type;
    }

    public Location(String name, String type) {
        this(name, null, type);
    }

    public enum LocationType {
        FIRE_STATION,
        EQUIPMENT_DEPOT,
        TRAINING_CENTER
    }
}
