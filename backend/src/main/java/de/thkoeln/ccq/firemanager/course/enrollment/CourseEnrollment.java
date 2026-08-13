package de.thkoeln.ccq.firemanager.course.enrollment;

import de.thkoeln.ccq.firemanager.course.Course;
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

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "course_enrollments")
@Getter
public class CourseEnrollment {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    @Setter
    private UUID courseId;

    @Column(nullable = false)
    @Setter
    private String courseName;

    @Column(nullable = false)
    @Setter
    private UUID memberId;

    @Column(nullable = false)
    @Setter
    private String memberName;

    @Column(nullable = false)
    @Setter
    private String status;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Setter
    private String comment;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime enrolledAt;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime confirmedAt;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime updatedAt;

    protected CourseEnrollment() {
        // JPA only
    }

    public CourseEnrollment(
            UUID courseId,
            String courseName,
            UUID memberId,
            String memberName,
            String status,
            String comment,
            OffsetDateTime enrolledAt,
            OffsetDateTime confirmedAt
    ) {
        if (courseId == null) {
            throw new IllegalArgumentException("courseId must not be null");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("memberId must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        this.id = UUID.randomUUID();
        this.courseId = courseId;
        this.courseName = courseName;
        this.memberId = memberId;
        this.memberName = memberName;
        this.status = status;
        this.comment = comment;
        this.enrolledAt = enrolledAt;
        this.confirmedAt = confirmedAt;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public CourseEnrollment(
            UUID courseId,
            String courseName,
            UUID memberId,
            String memberName,
            String status,
            String comment
    ) {
        this(
                courseId,
                courseName,
                memberId,
                memberName,
                status,
                comment,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
    }

    public CourseEnrollment(UUID courseId, String courseName, UUID memberId, String memberName, String status) {
        this(courseId, courseName, memberId, memberName, status, null);
    }
}