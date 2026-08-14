package de.thkoeln.ccq.firemanager.memberqualification;

import de.thkoeln.ccq.firemanager.course.Course;
import de.thkoeln.ccq.firemanager.course.CourseServiceInterface;
import de.thkoeln.ccq.firemanager.memberqualification.exception.MemberQualificationConflictException;
import de.thkoeln.ccq.firemanager.memberqualification.exception.MemberQualificationNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MemberQualificationService {

    private final MemberQualificationRepository memberQualificationRepository;
    private final CourseServiceInterface courseService;
    private final Clock clock;

    public MemberQualificationService(
            MemberQualificationRepository memberQualificationRepository,
            CourseServiceInterface courseService,
            Clock clock
    ) {
        this.memberQualificationRepository = memberQualificationRepository;
        this.courseService = courseService;
        this.clock = clock;
    }

    public MemberQualification assignQualification(
            UUID memberId, 
            UUID courseId, 
            LocalDate acquisitionDate, 
            String certificateNumber, 
            String issuingAuthority
    ) {
        // Prüfen ob das Mitglied die Qualifikation bereits besitzt
        if (memberQualificationRepository.existsByMemberIdAndCourseIdAndWithdrawnFalse(memberId, courseId)) {
            throw new MemberQualificationConflictException("Member already has this qualification");
        }

        Course course = courseService.getById(courseId);
        
        MemberQualification qualification = new MemberQualification(
                memberId,
                courseId,
                course.getName(),
                course.getCategory().toString(),
                acquisitionDate,
                certificateNumber,
                issuingAuthority
        );
        
        return memberQualificationRepository.save(qualification);
    }

    public List<MemberQualification> getAllByMemberId(UUID memberId) {
        return memberQualificationRepository.findByMemberId(memberId);
    }

    public List<MemberQualification> getByMemberIdAndCategory(UUID memberId, String category) {
        return memberQualificationRepository.findByMemberIdAndCourseCategory(memberId, category);
    }

    public List<MemberQualification> getByMemberIdAndWithdrawn(UUID memberId, boolean withdrawn) {
        return memberQualificationRepository.findByMemberIdAndWithdrawn(memberId, withdrawn);
    }

    public MemberQualification getById(UUID qualificationId) {
        if (qualificationId == null) {
            throw new IllegalArgumentException("qualificationId must not be null");
        }
        return memberQualificationRepository.findById(qualificationId)
                .orElseThrow(() -> new MemberQualificationNotFoundException(qualificationId));
    }

    public void withdrawQualification(UUID qualificationId, String withdrawalReason) {
        MemberQualification qualification = getById(qualificationId);
        
        if (qualification.isWithdrawn()) {
            throw new MemberQualificationConflictException("Qualification is already withdrawn");
        }
        
        qualification.withdraw(withdrawalReason);
        memberQualificationRepository.save(qualification);
    }

    public boolean existsByCourseId(UUID courseId) {
        return memberQualificationRepository.existsByMemberIdAndCourseIdAndWithdrawnFalse(null, courseId);
    }

    public boolean hasMemberAllQualifications(
            UUID memberId, 
            List<UUID> courseIds
    ) {
        List<MemberQualification> qualifications = 
                memberQualificationRepository
                        .findByMemberIdAndWithdrawnFalse(
                                memberId, 
                                false
                        );
        
        for (UUID courseId : courseIds) {
            boolean hasQualification = qualifications.stream()
                    .anyMatch(q -> q.getCourseId().equals(courseId));
            
            if (!hasQualification) {
                return false;
            }
        }
        
        return true;
    }
}
