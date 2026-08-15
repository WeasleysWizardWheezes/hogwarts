package de.thkoeln.ccq.firemanager.location;

import de.thkoeln.ccq.firemanager.generated.api.LocationsApi;
import de.thkoeln.ccq.firemanager.generated.model.CreateLocationRequest;
import de.thkoeln.ccq.firemanager.generated.model.ListLocations200Response;
import de.thkoeln.ccq.firemanager.generated.model.LocationResponse;
import de.thkoeln.ccq.firemanager.generated.model.UpdateLocationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class LocationController implements LocationsApi {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public ResponseEntity<LocationResponse> createLocation(CreateLocationRequest request) {
        Location location = this.locationService.create(
                request.getName(),
                request.getAddress(),
                request.getType().getValue()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(location));
    }

    @Override
    public ResponseEntity<Void> deleteLocation(UUID locationId) {
        this.locationService.deleteById(locationId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<LocationResponse> getLocation(UUID locationId) {
        Location location = this.locationService.getById(locationId);
        return ResponseEntity.ok(toResponse(location));
    }

    @Override
    public ResponseEntity<ListLocations200Response> listLocations(Integer page, Integer size) {
        ListLocations200Response response = new ListLocations200Response();
        this.locationService.getAll().forEach(location ->
                response.addDataItem(toResponse(location))
        );
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LocationResponse> updateLocation(UUID locationId, UpdateLocationRequest request) {
        Location location = this.locationService.update(
                locationId,
                request.getName(),
                request.getAddress(),
                request.getType() != null ? request.getType().getValue() : null
        );
        return ResponseEntity.ok(toResponse(location));
    }

    private LocationResponse toResponse(Location location) {
        LocationResponse response = new LocationResponse();
        response.setId(location.getId());
        response.setName(location.getName());
        response.setAddress(location.getAddress());
        response.setType(mapStringToResponseType(location.getType()));
        return response;
    }

    private LocationResponse.TypeEnum mapStringToResponseType(String type) {
        if (type == null) {
            return null;
        }
        return switch (type) {
            case "FIRE_STATION" -> LocationResponse.TypeEnum.FIRE_STATION;
            case "EQUIPMENT_DEPOT" -> LocationResponse.TypeEnum.EQUIPMENT_DEPOT;
            case "TRAINING_CENTER" -> LocationResponse.TypeEnum.TRAINING_CENTER;
            default -> LocationResponse.TypeEnum.fromValue(type);
        };
    }
}
