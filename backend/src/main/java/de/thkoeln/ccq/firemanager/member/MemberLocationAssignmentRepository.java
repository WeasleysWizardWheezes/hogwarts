package de.thkoeln.ccq.firemanager.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MemberLocationAssignmentRepository extends JpaRepository<MemberLocationAssignment, UUID> {

    List<MemberLocationAssignment> findByLocationId(UUID locationId);

    List<MemberLocationAssignment> findByMemberId(UUID memberId);

    boolean existsByMemberId(UUID memberId);

}