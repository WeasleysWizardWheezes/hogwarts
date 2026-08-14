package de.thkoeln.ccq.firemanager.memberqualification;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
    @Query("SELECT COUNT(DISTINCT q.courseId) = COUNT(c) " +
            "FROM MemberQualification q " +
            "WHERE q.memberId = :memberId " +
            "AND q.courseId IN :courseIds " +
            "AND q.withdrawn = false")
    boolean hasMemberAllQualifications(@Param("memberId") UUID memberId, 
            @Param("courseIds") List<UUID> courseIds);
}
