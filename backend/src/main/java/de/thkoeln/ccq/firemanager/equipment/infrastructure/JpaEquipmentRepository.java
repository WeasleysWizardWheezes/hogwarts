package de.thkoeln.ccq.firemanager.equipment.infrastructure;

import de.thkoeln.ccq.firemanager.equipment.domain.Equipment;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentId;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaEquipmentRepository implements EquipmentRepository {
    private final SpringDataEquipmentRepository springDataEquipmentRepository;
    private final EquipmentPersistenceMapper mapper;

    public JpaEquipmentRepository(
        SpringDataEquipmentRepository springDataEquipmentRepository,
        EquipmentPersistenceMapper mapper
    ) {
        this.springDataEquipmentRepository = springDataEquipmentRepository;
        this.mapper = mapper;
    }

    @Override
    public Equipment save(Equipment equipment) {
        EquipmentJpaEntity entity = mapper.toEntity(equipment);
        EquipmentJpaEntity savedEntity = springDataEquipmentRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Equipment> findById(EquipmentId id) {
        return springDataEquipmentRepository.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public List<Equipment> findAll() {
        return springDataEquipmentRepository.findAll()
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Equipment> findByName(String name) {
        return springDataEquipmentRepository.findByNameContainingIgnoreCase(name)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Equipment> findBySerialNumber(String serialNumber) {
        return springDataEquipmentRepository.findBySerialNumberContainingIgnoreCase(serialNumber)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Equipment> findByStorageLocation(String storageLocation) {
        return springDataEquipmentRepository.findByStorageLocationContainingIgnoreCase(storageLocation)
            .stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void delete(EquipmentId id) {
        springDataEquipmentRepository.deleteById(id.value());
    }
}
