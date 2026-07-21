package de.thkoeln.ccq.firemanager.locations.infrastructure;

import de.thkoeln.ccq.firemanager.locations.application.LocationService;
import de.thkoeln.ccq.firemanager.locations.domain.LocationType;
import java.util.List;
import java.util.UUID;
import org.openapitools.api.LocationsApi;
import org.openapitools.model.CreateLocationRequest;
import org.openapitools.model.ListLocations200Response;
import org.openapitools.model.LocationResponse;
import org.openapitools.model.PaginationMeta;
import org.openapitools.model.UpdateLocationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LocationController implements LocationsApi {
    private static org.openapitools.model.LocationType mapLocationType(
            de.thkoeln.ccq.firemanager.locations.domain.LocationType type) {
        if (type == null) {
            return null;
        }
        return org.openapitools.model.LocationType.valueOf(type.name());
    }

    private static de.thkoeln.ccq.firemanager.locations.domain.LocationType mapLocationType(
            org.openapitools.model.LocationType type) {
        if (type == null) {
            return null;
        }
        return de.thkoeln.ccq.firemanager.locations.domain.LocationType.valueOf(type.name());
    }

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @Override
    public ResponseEntity<LocationResponse> createLocation(CreateLocationRequest createLocationRequest) {
        de.thkoeln.ccq.firemanager.locations.domain.Location location = locationService.createLocation(
                createLocationRequest.getName(),
                mapLocationType(createLocationRequest.getType()),
                createLocationRequest.getParentLocationId()
        );

        LocationResponse response = new LocationResponse();
        response.setId(location.id());
        response.setName(location.name());
        response.setType(mapLocationType(location.type()));
        response.setParentLocationId(location.parentLocationId());
        response.setParentLocationName(location.parentLocationName());
        response.setCreatedAt(location.createdAt());
        response.setUpdatedAt(location.updatedAt());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> deleteLocation(UUID id) {
        locationService.deleteLocation(id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<LocationResponse> getLocation(UUID id) {
        de.thkoeln.ccq.firemanager.locations.domain.Location location = locationService.getLocation(id);

        LocationResponse response = new LocationResponse();
        response.setId(location.id());
        response.setName(location.name());
        response.setType(mapLocationType(location.type()));
        response.setParentLocationId(location.parentLocationId());
        response.setParentLocationName(location.parentLocationName());
        response.setCreatedAt(location.createdAt());
        response.setUpdatedAt(location.updatedAt());

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListLocations200Response> listLocations(
            Integer page, Integer size, org.openapitools.model.LocationType type) {
        List<de.thkoeln.ccq.firemanager.locations.domain.Location> locations = locationService
                .listLocations(page, size, mapLocationType(type));
        ListLocations200Response response = new ListLocations200Response();
        List<LocationResponse> locationResponses = locations.stream()
                .map(location -> {
                    LocationResponse locationResponse = new LocationResponse();
                    locationResponse.setId(location.id());
                    locationResponse.setName(location.name());
                    locationResponse.setType(mapLocationType(location.type()));
                    locationResponse.setParentLocationId(location.parentLocationId());
                    locationResponse.setParentLocationName(location.parentLocationName());
                    locationResponse.setCreatedAt(location.createdAt());
                    locationResponse.setUpdatedAt(location.updatedAt());
                    return locationResponse;
                })
                .toList();
        response.setData(locationResponses);

        PaginationMeta paginationMeta = new PaginationMeta();
        paginationMeta.setPage(page != null ? page : 0);
        paginationMeta.setSize(size != null ? size : locations.size());
        paginationMeta.setTotalElements(locations.size());
        paginationMeta.setTotalPages(1);
        response.setPage(paginationMeta);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<LocationResponse> updateLocation(UUID id, UpdateLocationRequest updateLocationRequest) {
        de.thkoeln.ccq.firemanager.locations.domain.Location location = locationService.updateLocation(
                id,
                updateLocationRequest.getName(),
                mapLocationType(updateLocationRequest.getType()),
                updateLocationRequest.getParentLocationId()
        );

        LocationResponse response = new LocationResponse();
        response.setId(location.id());
        response.setName(location.name());
        response.setType(mapLocationType(location.type()));
        response.setParentLocationId(location.parentLocationId());
        response.setParentLocationName(location.parentLocationName());
        response.setCreatedAt(location.createdAt());
        response.setUpdatedAt(location.updatedAt());

        return ResponseEntity.ok(response);
    }
}
