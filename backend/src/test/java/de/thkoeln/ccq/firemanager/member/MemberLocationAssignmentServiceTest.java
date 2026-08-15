package de.thkoeln.ccq.firemanager.member;

import de.thkoeln.ccq.firemanager.location.Location;
import de.thkoeln.ccq.firemanager.location.LocationRepository;
import de.thkoeln.ccq.firemanager.member.exception.MemberLocationAssignmentConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
class MemberLocationAssignmentServiceTest {

    @Mock
    private MemberLocationAssignmentRepository memberLocationAssignmentRepositoryStub;

    @Mock
    private LocationRepository locationRepositoryStub;

    @InjectMocks
    private MemberLocationAssignmentService sut;

    @BeforeEach
    void setUp() {
        // Common setup if needed
    }

    @Test
    void assignMemberToLocation_returnsAssignmentWhenSuccessful() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        UUID locationId = location.getId();
        
        when(locationRepositoryStub.findById(locationId)).thenReturn(Optional.of(location));
        when(memberLocationAssignmentRepositoryStub.existsByMemberId(memberId)).thenReturn(false);
        
        MemberLocationAssignment expectedAssignment = new MemberLocationAssignment(memberId, location);
        when(memberLocationAssignmentRepositoryStub.save(any(MemberLocationAssignment.class)))
                .thenReturn(expectedAssignment);

        // Act
        MemberLocationAssignment result = sut.assignMemberToLocation(memberId, locationId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getMemberId()).isEqualTo(memberId);
        assertThat(result.getLocation()).isEqualTo(location);
    }

    @Test
    void assignMemberToLocation_throwsExceptionWhenMemberAlreadyAssigned() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        UUID locationId = UUID.randomUUID();
        
        when(memberLocationAssignmentRepositoryStub.existsByMemberId(memberId)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> sut.assignMemberToLocation(memberId, locationId))
                .isInstanceOf(MemberLocationAssignmentConflictException.class)
                .hasMessage("Member is already assigned to a location");
    }

    @Test
    void getAssignmentsByLocation_returnsAssignmentsForLocation() {
        // Arrange
        UUID locationId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        
        MemberLocationAssignment assignment1 = new MemberLocationAssignment(UUID.randomUUID(), location);
        MemberLocationAssignment assignment2 = new MemberLocationAssignment(UUID.randomUUID(), location);
        
        when(memberLocationAssignmentRepositoryStub.findByLocationId(locationId))
                .thenReturn(List.of(assignment1, assignment2));

        // Act
        List<MemberLocationAssignment> result = sut.getAssignmentsByLocation(locationId);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyInAnyOrder(assignment1, assignment2);
    }

    @Test
    void getAssignmentsByMember_returnsAssignmentsForMember() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        
        MemberLocationAssignment assignment = new MemberLocationAssignment(memberId, location);
        
        when(memberLocationAssignmentRepositoryStub.findByMemberId(memberId))
                .thenReturn(List.of(assignment));

        // Act
        List<MemberLocationAssignment> result = sut.getAssignmentsByMember(memberId);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(assignment);
    }

    @Test
    void deleteAssignmentByMember_deletesAssignmentsWhenExist() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        Location location = new Location("Gerätehaus Köln", "Musterstraße 1, 50677 Köln", "FIRE_STATION");
        
        MemberLocationAssignment assignment = new MemberLocationAssignment(memberId, location);
        
        when(memberLocationAssignmentRepositoryStub.findByMemberId(memberId))
                .thenReturn(List.of(assignment));

        // Act
        sut.deleteAssignmentByMember(memberId);

        // Assert
        verify(memberLocationAssignmentRepositoryStub).deleteAll(List.of(assignment));
    }

    @Test
    void deleteAssignmentByMember_doesNothingWhenNoAssignmentsExist() {
        // Arrange
        UUID memberId = UUID.randomUUID();
        
        when(memberLocationAssignmentRepositoryStub.findByMemberId(memberId))
                .thenReturn(List.of());

        // Act
        sut.deleteAssignmentByMember(memberId);

        // Assert
        verify(memberLocationAssignmentRepositoryStub, never()).deleteAll(any());
    }
}