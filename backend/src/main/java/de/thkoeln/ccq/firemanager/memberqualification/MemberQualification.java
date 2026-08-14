package de.thkoeln.ccq.firemanager.memberqualification;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.util.Assert;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "member_qualifications")
@Getter
public class MemberQualification {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, name = "member_id")
    @Setter(AccessLevel.PRIVATE)
    private UUID memberId;

    @Column(nullable = false, name = "course_id")
    @Setter(AccessLevel.PRIVATE)
    private UUID courseId;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private String courseName;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private String courseCategory;

    @Column(nullable = false)
    @Setter
    private LocalDate acquisitionDate;

    @Column
    @Setter
    private String certificateNumber;

    @Column
    @Setter
    private String issuingAuthority;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private boolean withdrawn;

    @Column
    @Setter(AccessLevel.PRIVATE)
    private LocalDate withdrawalDate;

    @Column
    @Setter(AccessLevel.PRIVATE)
    private String withdrawalReason;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;

    protected MemberQualification() {
        // JPA only
    }

    public MemberQualification(
            UUID memberId,
            UUID courseId,
            String courseName,
            String courseCategory,
            LocalDate acquisitionDate,
            String certificateNumber,
            String issuingAuthority
    ) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId must not be null");
        }
        if (courseId == null) {
            throw new IllegalArgumentException("courseId must not be null");
        }
        Assert.hasText(courseName, "courseName must not be empty");
        Assert.hasText(courseCategory, "courseCategory must not be empty");
        if (acquisitionDate == null) {
            throw new IllegalArgumentException("acquisitionDate must not be null");
        }

        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.courseId = courseId;
        this.courseName = courseName;
        this.courseCategory = courseCategory;
        this.acquisitionDate = acquisitionDate;
        this.certificateNumber = certificateNumber;
        this.issuingAuthority = issuingAuthority;
        this.withdrawn = false;
        this.withdrawalDate = null;
        this.withdrawalReason = null;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void withdraw(String withdrawalReason) {
        Assert.hasText(
                withdrawalReason,
                "withdrawalReason must not be empty"
        );

        this.withdrawn = true;
        this.withdrawalDate = LocalDate.now();
        this.withdrawalReason = withdrawalReason;
        this.updatedAt = LocalDateTime.now();
    }
}
