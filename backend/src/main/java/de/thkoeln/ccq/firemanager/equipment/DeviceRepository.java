package de.thkoeln.ccq.firemanager.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DeviceRepository extends JpaRepository<Device, UUID>, JpaSpecificationExecutor<Device> {

    Optional<Device> findBySerialNumber(String serialNumber);

    Optional<Device> findByIdAndArchivedFalse(UUID id);

    Optional<Device> findBySerialNumberAndArchivedFalse(String serialNumber);

    default Optional<Device> findActiveById(UUID id) {
        return findByIdAndArchivedFalse(id);
    }

    default Optional<Device> findActiveBySerialNumber(String serialNumber) {
        return findBySerialNumberAndArchivedFalse(serialNumber);
    }

    List<Device> findAllByArchivedFalse();
}