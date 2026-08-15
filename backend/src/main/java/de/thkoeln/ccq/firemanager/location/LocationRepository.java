package de.thkoeln.ccq.firemanager.location;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LocationRepository extends JpaRepository<Location, UUID> {
    boolean existsByName(String name);
}
