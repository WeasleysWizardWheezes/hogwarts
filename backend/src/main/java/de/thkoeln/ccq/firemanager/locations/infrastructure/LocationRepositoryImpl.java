package de.thkoeln.ccq.firemanager.locations.infrastructure;

import de.thkoeln.ccq.firemanager.locations.domain.Location;
import de.thkoeln.ccq.firemanager.locations.domain.LocationRepository;
import de.thkoeln.ccq.firemanager.locations.domain.LocationType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class LocationRepositoryImpl implements LocationRepository {
    private final LocationJpaRepository locationJpaRepository;

    public LocationRepositoryImpl(LocationJpaRepository locationJpaRepository) {
        this.locationJpaRepository = locationJpaRepository;
    }

    @Override
    public Location save(Location location) {
        LocationEntity entity = LocationEntity.fromDomain(location);
        LocationEntity savedEntity = locationJpaRepository.save(entity);
        return savedEntity.toDomain();
    }

    @Override
    public Optional<Location> findById(UUID id) {
        return locationJpaRepository.findById(id).map(LocationEntity::toDomain);
    }

    @Override
    public List<Location> findAll(Integer page, Integer size, LocationType type) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size != null ? size : Integer.MAX_VALUE);
        if (type == null) {
            return locationJpaRepository.findAll(pageable).stream()
                .map(LocationEntity::toDomain)
                .toList();
        } else {
            return locationJpaRepository.findAllByType(type, pageable).stream()
                .map(LocationEntity::toDomain)
                .toList();
        }
    }

    @Override
    public void deleteById(UUID id) {
        locationJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(UUID id) {
        return locationJpaRepository.existsById(id);
    }
}
