package de.thkoeln.ccq.firemanager.member;

import org.openapitools.api.MembersApi;
import org.openapitools.model.MemberLocationAssignmentRequest;
import org.openapitools.model.MemberResponse;
import org.openapitools.model.ListMembers200Response;
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
        var locationResponse = new org.openapitools.model.LocationResponse()
                .id(assignment.getLocation().getId())
                .name(assignment.getLocation().getName())
                .address(assignment.getLocation().getAddress())
                .type(org.openapitools.model.LocationResponse.TypeEnum.fromValue(assignment.getLocation().getType()));
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
        var locationResponse = new org.openapitools.model.LocationResponse()
                .id(assignment.getLocation().getId())
                .name(assignment.getLocation().getName())
                .address(assignment.getLocation().getAddress())
                .type(org.openapitools.model.LocationResponse.TypeEnum.fromValue(assignment.getLocation().getType()));
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
                    var locResponse = new org.openapitools.model.LocationResponse()
                            .id(assignment.getLocation().getId())
                            .name(assignment.getLocation().getName())
                            .address(assignment.getLocation().getAddress())
                            .type(org.openapitools.model.LocationResponse.TypeEnum
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