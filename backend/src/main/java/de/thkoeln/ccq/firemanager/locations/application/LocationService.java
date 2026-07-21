package de.thkoeln.ccq.firemanager.locations.application;

import de.thkoeln.ccq.firemanager.locations.domain.Location;
import de.thkoeln.ccq.firemanager.locations.domain.LocationRepository;
import de.thkoeln.ccq.firemanager.locations.domain.LocationType;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class LocationService {
    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location createLocation(String name, LocationType type, UUID parentLocationId) {
        if (parentLocationId != null && !locationRepository.existsById(parentLocationId)) {
            throw new IllegalArgumentException("Parent location does not exist");
        }

        String parentLocationName = null;
        if (parentLocationId != null) {
            parentLocationName = locationRepository.findById(parentLocationId)
                    .map(Location::name)
                    .orElse(null);
        }

        OffsetDateTime now = OffsetDateTime.now();
        Location location = new Location(
                UUID.randomUUID(),
                name,
                type,
                parentLocationId,
                parentLocationName,
                now,
                now
        );

        return locationRepository.save(location);
    }

    public Location getLocation(UUID id) {
        return locationRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Location not found"));
    }

    public List<Location> listLocations(Integer page, Integer size, LocationType type) {
        return locationRepository.findAll(page, size, type);
    }

    public Location updateLocation(UUID id, String name, LocationType type, UUID parentLocationId) {
        Location existingLocation = locationRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Location not found"));

        if (parentLocationId != null && !locationRepository.existsById(parentLocationId)) {
            throw new IllegalArgumentException("Parent location does not exist");
        }

        String parentLocationName = null;
        if (parentLocationId != null) {
            parentLocationName = locationRepository.findById(parentLocationId)
                    .map(Location::name)
                    .orElse(null);
        }

        OffsetDateTime now = OffsetDateTime.now();
        Location updatedLocation = new Location(
                id,
                name != null ? name : existingLocation.name(),
                type != null ? type : existingLocation.type(),
                parentLocationId,
                parentLocationName,
                existingLocation.createdAt(),
                now
        );

        return locationRepository.save(updatedLocation);
    }

    public void deleteLocation(UUID id) {
        if (!locationRepository.existsById(id)) {
            throw new IllegalArgumentException("Location not found");
        }
        locationRepository.deleteById(id);
    }
}
