package de.thkoeln.ccq.firemanager.location;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    @Column(nullable = false)
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
        Assert.hasText(address, "address must not be empty");
        Assert.hasText(type, "type must not be empty");

        this.id = UUID.randomUUID();
        this.name = name;
        this.address = address;
        this.type = type;
    }
}