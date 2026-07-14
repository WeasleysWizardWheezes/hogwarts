package de.thkoeln.ccq.firemanager.equipment.infrastructure;

import de.thkoeln.ccq.firemanager.equipment.domain.Equipment;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentId;
import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaEquipmentRepository implements EquipmentRepository {

    private final SpringDataEquipmentRepository springDataRepository;
    private final EquipmentPersistenceMapper mapper;

    @Override
    public Equipment save(Equipment equipment) {
        var entity = mapper.toEntity(equipment);
        var savedEntity = springDataRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Equipment> findById(EquipmentId id) {
        return springDataRepository.findById(id.value())
            .map(mapper::toDomain);
    }

    @Override
    public List<Equipment> findAll() {
        return springDataRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Equipment> findByLocation(String location) {
        return springDataRepository.findByLocation(location).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public List<Equipment> searchByNameOrSerialNumber(String searchTerm) {
        return springDataRepository.searchByNameOrSerialNumber(searchTerm).stream()
            .map(mapper::toDomain)
            .toList();
    }

    @Override
    public void delete(EquipmentId id) {
        springDataRepository.deleteById(id.value());
    }

    @Override
    public boolean existsBySerialNumber(String serialNumber) {
        return springDataRepository.existsBySerialNumber(serialNumber);
    }
}