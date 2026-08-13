package de.thkoeln.ccq.firemanager.location;

import de.thkoeln.ccq.firemanager.generated.api.LocationsApi;
import de.thkoeln.ccq.firemanager.generated.model.CreateLocationRequest;
import de.thkoeln.ccq.firemanager.generated.model.LocationResponse;
import de.thkoeln.ccq.firemanager.generated.model.ListLocations200Response;
import de.thkoeln.ccq.firemanager.generated.model.UpdateLocationRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class LocationController implements LocationsApi {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public ResponseEntity<LocationResponse> createLocation(CreateLocationRequest createLocationRequest) {
        Location location = locationService.create(
                createLocationRequest.getName(),
                createLocationRequest.getAddress(),
                createLocationRequest.getType().toString()
        );
        LocationResponse response = new LocationResponse()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .type(LocationResponse.TypeEnum.fromValue(location.getType()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Override
    public ResponseEntity<Void> deleteLocation(UUID locationId) {
        locationService.deleteById(locationId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<LocationResponse> getLocation(UUID locationId) {
        Location location = locationService.getById(locationId);
        LocationResponse response = new LocationResponse()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .type(LocationResponse.TypeEnum.fromValue(location.getType()));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListLocations200Response> listLocations(Integer limit, Integer offset) {
        List<Location> locations = locationService.getAll();
        List<LocationResponse> locationResponses = locations.stream()
                .map(location -> {
                    var locResponse = new LocationResponse()
                            .id(location.getId())
                            .name(location.getName())
                            .address(location.getAddress())
                            .type(LocationResponse.TypeEnum.fromValue(location.getType()));
                    return locResponse;
                })
                .toList();
        ListLocations200Response response = new ListLocations200Response()
                .data(locationResponses);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LocationResponse> updateLocation(
            UUID locationId, 
            UpdateLocationRequest updateLocationRequest
    ) {
        Location location = locationService.update(
                locationId,
                updateLocationRequest.getName(),
                updateLocationRequest.getAddress(),
                updateLocationRequest.getType().toString()
        );
        LocationResponse response = new LocationResponse()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .type(LocationResponse.TypeEnum.fromValue(location.getType()));
        return ResponseEntity.ok(response);
    }
}