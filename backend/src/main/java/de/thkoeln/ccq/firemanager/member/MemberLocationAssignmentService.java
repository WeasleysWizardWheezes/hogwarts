package de.thkoeln.ccq.firemanager.member;

import de.thkoeln.ccq.firemanager.location.Location;
import de.thkoeln.ccq.firemanager.location.LocationService;
import de.thkoeln.ccq.firemanager.member.exception.MemberLocationAssignmentConflictException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MemberLocationAssignmentService {

    private final MemberLocationAssignmentRepository memberLocationAssignmentRepository;
    private final LocationService locationService;

    public MemberLocationAssignmentService(
            MemberLocationAssignmentRepository memberLocationAssignmentRepository,
            LocationService locationService
    ) {
        this.memberLocationAssignmentRepository = memberLocationAssignmentRepository;
        this.locationService = locationService;
    }

    public MemberLocationAssignment assignMemberToLocation(UUID memberId, UUID locationId) {
        if (memberLocationAssignmentRepository.existsByMemberId(memberId)) {
            throw new MemberLocationAssignmentConflictException("Member is already assigned to a location");
        }

        Location location = locationService.getById(locationId);
        MemberLocationAssignment assignment = new MemberLocationAssignment(memberId, location);
        return this.memberLocationAssignmentRepository.save(assignment);
    }

    public List<MemberLocationAssignment> getAssignmentsByLocation(UUID locationId) {
        return this.memberLocationAssignmentRepository.findByLocationId(locationId);
    }

    public List<MemberLocationAssignment> getAssignmentsByMember(UUID memberId) {
        return this.memberLocationAssignmentRepository.findByMemberId(memberId);
    }

    public void deleteAssignmentByMember(UUID memberId) {
        List<MemberLocationAssignment> assignments = memberLocationAssignmentRepository.findByMemberId(memberId);
        if (!assignments.isEmpty()) {
            memberLocationAssignmentRepository.deleteAll(assignments);
        }
    }
}