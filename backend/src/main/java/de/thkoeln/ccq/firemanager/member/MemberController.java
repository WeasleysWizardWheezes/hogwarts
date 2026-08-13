package de.thkoeln.ccq.firemanager.member;

import de.thkoeln.ccq.firemanager.generated.api.MembersApi;
import de.thkoeln.ccq.firemanager.generated.model.LocationResponse;
import de.thkoeln.ccq.firemanager.generated.model.MemberLocationAssignmentRequest;
import de.thkoeln.ccq.firemanager.generated.model.MemberResponse;
import de.thkoeln.ccq.firemanager.generated.model.ListMembers200Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class MemberController implements MembersApi {

    private final MemberLocationAssignmentService memberLocationAssignmentService;

    public MemberController(MemberLocationAssignmentService memberLocationAssignmentService) {
        this.memberLocationAssignmentService = memberLocationAssignmentService;
    }

    @Override
    public ResponseEntity<MemberResponse> assignMemberToLocation(
            UUID memberId, 
            MemberLocationAssignmentRequest memberLocationAssignmentRequest
    ) {
        MemberLocationAssignment assignment = memberLocationAssignmentService.assignMemberToLocation(
                memberId,
                memberLocationAssignmentRequest.getLocationId()
        );
        var locationResponse = new LocationResponse()
                .id(assignment.getLocation().getId())
                .name(assignment.getLocation().getName())
                .address(assignment.getLocation().getAddress())
                .type(LocationResponse.TypeEnum.fromValue(assignment.getLocation().getType()));
        MemberResponse response = new MemberResponse()
                .id(assignment.getMemberId())
                .addLocationsItem(locationResponse);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<MemberResponse> getMember(UUID memberId) {
        List<MemberLocationAssignment> assignments = memberLocationAssignmentService.getAssignmentsByMember(memberId);
        if (assignments.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        MemberLocationAssignment assignment = assignments.get(0);
        var locationResponse = new LocationResponse()
                .id(assignment.getLocation().getId())
                .name(assignment.getLocation().getName())
                .address(assignment.getLocation().getAddress())
                .type(LocationResponse.TypeEnum.fromValue(assignment.getLocation().getType()));
        MemberResponse response = new MemberResponse()
                .id(assignment.getMemberId())
                .addLocationsItem(locationResponse);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ListMembers200Response> listMembers(
            Integer limit, 
            Integer offset, 
            UUID locationId
    ) {
        List<MemberLocationAssignment> assignments;
        if (locationId != null) {
            assignments = memberLocationAssignmentService.getAssignmentsByLocation(locationId);
        } else {
            assignments = memberLocationAssignmentService.getAssignmentsByMember(null);
        }
        List<MemberResponse> memberResponses = assignments.stream()
                .map(assignment -> {
                    var locResponse = new LocationResponse()
                            .id(assignment.getLocation().getId())
                            .name(assignment.getLocation().getName())
                            .address(assignment.getLocation().getAddress())
                            .type(LocationResponse.TypeEnum
                                    .fromValue(assignment.getLocation().getType()));
                    return new MemberResponse()
                            .id(assignment.getMemberId())
                            .addLocationsItem(locResponse);
                })
                .toList();
        ListMembers200Response response = new ListMembers200Response()
                .data(memberResponses);
        return ResponseEntity.ok(response);
    }
}