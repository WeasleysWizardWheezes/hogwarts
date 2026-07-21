package de.thkoeln.ccq.firemanager.locations.infrastructure;

import de.thkoeln.ccq.firemanager.locations.domain.Location;
import de.thkoeln.ccq.firemanager.locations.domain.LocationType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "locations")
public class LocationEntity {
    @Id
    private UUID id;
    private String name;
    @Enumerated(EnumType.STRING)
    private LocationType type;
    private UUID parentLocationId;
    private String parentLocationName;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public LocationEntity() {
    }

    public LocationEntity(
            UUID id,
            String name,
            LocationType type,
            UUID parentLocationId,
            String parentLocationName,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.parentLocationId = parentLocationId;
        this.parentLocationName = parentLocationName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static LocationEntity fromDomain(Location location) {
        return new LocationEntity(
                location.id(),
                location.name(),
                location.type(),
                location.parentLocationId(),
                location.parentLocationName(),
                location.createdAt(),
                location.updatedAt()
        );
    }

    public Location toDomain() {
        return new Location(
            id,
            name,
            type,
            parentLocationId,
            parentLocationName,
            createdAt,
            updatedAt
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocationType getType() {
        return type;
    }

    public void setType(LocationType type) {
        this.type = type;
    }

    public UUID getParentLocationId() {
        return parentLocationId;
    }

    public void setParentLocationId(UUID parentLocationId) {
        this.parentLocationId = parentLocationId;
    }

    public String getParentLocationName() {
        return parentLocationName;
    }

    public void setParentLocationName(String parentLocationName) {
        this.parentLocationName = parentLocationName;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
