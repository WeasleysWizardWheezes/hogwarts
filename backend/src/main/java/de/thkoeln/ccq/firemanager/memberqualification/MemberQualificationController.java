package de.thkoeln.ccq.firemanager.memberqualification;

import de.thkoeln.ccq.firemanager.memberqualification.dto.CreateMemberQualificationRequest;
import de.thkoeln.ccq.firemanager.memberqualification.dto.WithdrawQualificationRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/members/{memberId}/qualifications")
public class MemberQualificationController {

    private final MemberQualificationService memberQualificationService;

    public MemberQualificationController(MemberQualificationService memberQualificationService) {
        this.memberQualificationService = memberQualificationService;
    }

    @PostMapping
    public ResponseEntity<MemberQualification> assignQualification(
            @PathVariable UUID memberId,
            @Valid @RequestBody CreateMemberQualificationRequest request
    ) {
        var qualification = memberQualificationService
                .assignQualification(
                        memberId,
                        request.courseId(),
                        request.acquisitionDate(),
                        request.certificateNumber(),
                        request.issuingAuthority()
                );
        return ResponseEntity.created(
                URI.create("/api/v1/members/" + memberId + 
                        "/qualifications/" + qualification.getId())
        ).body(qualification);
    }

    @GetMapping
    public ResponseEntity<List<MemberQualification>> listMemberQualifications(
            @PathVariable UUID memberId,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean withdrawn,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        if (category != null) {
            var qualifications = memberQualificationService.getByMemberIdAndCategory(memberId, category);
            return ResponseEntity.ok(qualifications);
        } else if (withdrawn != null) {
            var qualifications = memberQualificationService.getByMemberIdAndWithdrawn(memberId, withdrawn);
            return ResponseEntity.ok(qualifications);
        } else {
            var qualifications = memberQualificationService.getAllByMemberId(memberId);
            return ResponseEntity.ok(qualifications);
        }
    }

    @DeleteMapping("/{qualificationId}")
    public ResponseEntity<Void> withdrawQualification(
            @PathVariable UUID memberId,
            @PathVariable UUID qualificationId,
            @Valid @RequestBody WithdrawQualificationRequest request
    ) {
        memberQualificationService.withdrawQualification(qualificationId, request.reason());
        return ResponseEntity.noContent().build();
    }
}
