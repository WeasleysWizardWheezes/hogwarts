package de.thkoeln.ccq.firemanager.memberqualification;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MemberQualificationRepository extends JpaRepository<MemberQualification, UUID> {
    
    List<MemberQualification> findByMemberId(UUID memberId);
    
    List<MemberQualification> findByMemberIdAndCourseId(UUID memberId, UUID courseId);
    
    List<MemberQualification> findByMemberIdAndWithdrawn(UUID memberId, boolean withdrawn);
    
    List<MemberQualification> findByMemberIdAndCourseCategory(UUID memberId, String courseCategory);
    
    boolean existsByMemberIdAndCourseIdAndWithdrawnFalse(UUID memberId, UUID courseId);
    
    List<MemberQualification> findByMemberIdAndWithdrawnFalse(UUID memberId, boolean withdrawn);
}
