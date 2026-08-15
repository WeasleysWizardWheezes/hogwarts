package de.thkoeln.ccq.firemanager.location;

import de.thkoeln.ccq.firemanager.location.exception.LocationConflictException;
import de.thkoeln.ccq.firemanager.location.exception.LocationNotFoundException;
import de.thkoeln.ccq.firemanager.member.MemberLocationAssignment;
import de.thkoeln.ccq.firemanager.member.MemberLocationAssignmentService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LocationService {

    private final LocationRepository locationRepository;
    private final MemberLocationAssignmentService memberLocationAssignmentService;

    public LocationService(
            LocationRepository locationRepository,
            MemberLocationAssignmentService memberLocationAssignmentService
    ) {
        this.locationRepository = locationRepository;
        this.memberLocationAssignmentService = memberLocationAssignmentService;
    }

    public Location create(String name, String address, String type) {
        if (locationRepository.existsByName(name)) {
            throw new LocationConflictException("Location with name '" + name + "' already exists");
        }
        Location location = new Location(name, address, type);
        return this.locationRepository.save(location);
    }

    public Location create(String name, String type) {
        return this.create(name, null, type);
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

    public Location update(UUID locationId, String name, String address, String type) {
        Location existingLocation = getById(locationId);
        
        if (name != null && !name.equals(existingLocation.getName())) {
            if (locationRepository.existsByName(name)) {
                throw new LocationConflictException("Location with name '" + name + "' already exists");
            }
            existingLocation.setName(name);
        }
        
        if (address != null) {
            existingLocation.setAddress(address);
        }
        
        if (type != null) {
            existingLocation.setType(type);
        }
        
        return this.locationRepository.save(existingLocation);
    }

    public void deleteById(UUID locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new LocationNotFoundException(locationId);
        }
        
        // Check if any members are assigned to this location
        List<MemberLocationAssignment> assignments =
                memberLocationAssignmentService.getAssignmentsByLocation(locationId);
        if (!assignments.isEmpty()) {
            throw new LocationConflictException("Cannot delete location with assigned members");
        }
        
        locationRepository.deleteById(locationId);
    }
}
