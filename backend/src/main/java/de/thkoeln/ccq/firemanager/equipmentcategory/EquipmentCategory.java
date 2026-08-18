package de.thkoeln.ccq.firemanager.equipmentcategory;

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
@Table(name = "equipment_category")
@Getter
public class EquipmentCategory {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    @Setter
    private String name;

    @Setter
    private String description;

    @Column(nullable = false)
    @Setter
    private boolean archived;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime updatedAt;

    protected EquipmentCategory() {
        // JPA only
    }

    public EquipmentCategory(String name, String description) {
        Assert.hasText(name, "name must not be empty");

        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.archived = false;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }
}
