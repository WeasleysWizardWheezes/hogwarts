package de.thkoeln.ccq.firemanager.equipment.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {

    Optional<Device> findBySerialNumber(String serialNumber);

    List<Device> findByNameContainingIgnoreCase(String name);

    List<Device> findBySerialNumberContainingIgnoreCase(String serialNumber);

    List<Device> findByTypeContainingIgnoreCase(String type);

    List<Device> findByLocationContainingIgnoreCase(String location);
}