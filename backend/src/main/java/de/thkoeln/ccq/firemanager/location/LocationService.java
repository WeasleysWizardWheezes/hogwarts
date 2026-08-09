package de.thkoeln.ccq.firemanager.location;

import de.thkoeln.ccq.firemanager.location.exception.LocationNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public Location create(String name, String address, String type) {
        Location location = new Location(name, address, type);
        return this.locationRepository.save(location);
    }

    public List<Location> getAll() {
        return this.locationRepository.findAll();
    }

    public Location getById(UUID locationId) {
        if (locationId == null) {
            throw new IllegalArgumentException("locationId must not be null");
        }
        return locationRepository.findById(locationId)
                .orElseThrow(() -> new LocationNotFoundException(locationId));
    }

    public void deleteById(UUID locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new LocationNotFoundException(locationId);
        }
        locationRepository.deleteById(locationId);
    }

    public Location update(UUID locationId, String name, String address, String type) {
        Location existingLocation = getById(locationId);
        existingLocation.setName(name);
        existingLocation.setAddress(address);
        existingLocation.setType(type);
        return this.locationRepository.save(existingLocation);
    }
}