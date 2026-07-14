package de.thkoeln.ccq.firemanager.equipment.infrastructure;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "equipment")
public class EquipmentJpaEntity {
    @Id
    private UUID id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, unique = true)
    private String serialNumber;
    
    @Column(nullable = false)
    private String type;
    
    @Column(nullable = false)
    private String storageLocation;
    
    @Column
    private String description;
    
    @Column
    private LocalDate acquisitionDate;
    
    @Column
    private String manufacturer;

    public EquipmentJpaEntity() {
    }

    public EquipmentJpaEntity(
        UUID id,
        String name,
        String serialNumber,
        String type,
        String storageLocation,
        String description,
        LocalDate acquisitionDate,
        String manufacturer
    ) {
        this.id = id;
        this.name = name;
        this.serialNumber = serialNumber;
        this.type = type;
        this.storageLocation = storageLocation;
        this.description = description;
        this.acquisitionDate = acquisitionDate;
        this.manufacturer = manufacturer;
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

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStorageLocation() {
        return storageLocation;
    }

    public void setStorageLocation(String storageLocation) {
        this.storageLocation = storageLocation;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAcquisitionDate() {
        return acquisitionDate;
    }

    public void setAcquisitionDate(LocalDate acquisitionDate) {
        this.acquisitionDate = acquisitionDate;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }
}
