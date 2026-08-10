package de.thkoeln.ccq.firemanager.location;

import de.thkoeln.ccq.firemanager.location.exception.LocationNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class LocationServiceTest {

    @Mock
    private LocationRepository locationRepositoryStub;

    @InjectMocks
    private LocationService sut;

    @BeforeEach
    void setUp() {
        // Common setup if needed
    }

    @Test
    void create_returnsLocationWithGeneratedId() {
        // Arrange
        String name = "Gerätehaus Köln";
        String address = "Musterstraße 1, 50677 Köln";
        String type = "FIRE_STATION";
        
        Location expectedLocation = new Location(name, address, type);
        when(locationRepositoryStub.save(any(Location.class))).thenReturn(expectedLocation);

        // Act
        Location result = sut.create(name, address, type);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isNotNull();
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getAddress()).isEqualTo(address);
        assertThat(result.getType()).isEqualTo(type);
    }

    @Test
    void getAll_returnsAllLocations() {
        // Arrange
        Location location1 = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        Location location2 = new Location("Gerätehaus Bonn", "Beispielweg 2, 53111 Bonn", "FIRE_STATION");
        when(locationRepositoryStub.findAll()).thenReturn(List.of(location1, location2));

        // Act
        List<Location> result = sut.getAll();

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(location1, location2);
    }

    @Test
    void getById_returnsLocationWhenIdExists() {
        // Arrange
        Location expectedLocation = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        when(locationRepositoryStub.findById(expectedLocation.getId())).thenReturn(Optional.of(expectedLocation));

        // Act
        Location result = sut.getById(expectedLocation.getId());

        // Assert
        assertThat(result).isEqualTo(expectedLocation);
    }

    @Test
    void getById_throwsExceptionWhenLocationNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(locationRepositoryStub.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.getById(nonExistentId))
                .isInstanceOf(LocationNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }

    @Test
    void getById_throwsExceptionWhenIdIsNull() {
        // Act & Assert
        assertThatThrownBy(() -> sut.getById(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("locationId must not be null");
    }

    @Test
    void deleteById_deletesLocationWhenExists() {
        // Arrange
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        UUID locationId = location.getId();
        when(locationRepositoryStub.existsById(locationId)).thenReturn(true);

        // Act
        sut.deleteById(locationId);

        // Assert
        verify(locationRepositoryStub).deleteById(locationId);
    }

    @Test
    void deleteById_throwsExceptionWhenLocationNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(locationRepositoryStub.existsById(nonExistentId)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> sut.deleteById(nonExistentId))
                .isInstanceOf(LocationNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }

    @Test
    void update_returnsUpdatedLocation() {
        // Arrange
        Location existingLocation = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        UUID locationId = existingLocation.getId();
        
        String newName = "Gerätehaus Köln-Zentrum";
        String newAddress = "Neue Straße 1, 50677 Köln";
        String newType = "FIRE_STATION";
        
        when(locationRepositoryStub.findById(locationId)).thenReturn(Optional.of(existingLocation));
        when(locationRepositoryStub.save(any(Location.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Location result = sut.update(locationId, newName, newAddress, newType);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(locationId);
        assertThat(result.getName()).isEqualTo(newName);
        assertThat(result.getAddress()).isEqualTo(newAddress);
        assertThat(result.getType()).isEqualTo(newType);
    }

    @Test
    void update_throwsExceptionWhenLocationNotFound() {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(locationRepositoryStub.findById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> sut.update(nonExistentId, "New Name", "New Address", "FIRE_STATION"))
                .isInstanceOf(LocationNotFoundException.class)
                .hasMessageContaining(nonExistentId.toString());
    }
}