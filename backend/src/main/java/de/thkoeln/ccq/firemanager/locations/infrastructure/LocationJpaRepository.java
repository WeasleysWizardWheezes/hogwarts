package de.thkoeln.ccq.firemanager.locations.infrastructure;

import de.thkoeln.ccq.firemanager.locations.domain.LocationType;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LocationJpaRepository extends JpaRepository<LocationEntity, UUID> {
    @Query("SELECT l FROM LocationEntity l WHERE (:type IS NULL OR l.type = :type)")
    List<LocationEntity> findAllByType(@Param("type") LocationType type, Pageable pageable);
}
