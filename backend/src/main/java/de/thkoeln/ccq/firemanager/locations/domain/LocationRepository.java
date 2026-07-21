package de.thkoeln.ccq.firemanager.locations.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LocationRepository {

    Location save(Location location);

    Optional<Location> findById(UUID id);

    List<Location> findAll(Integer page, Integer size, LocationType type);

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
