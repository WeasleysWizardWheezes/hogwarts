package de.thkoeln.ccq.firemanager.course;

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
@Table(name = "courses")
@Getter
public class Course {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    @Setter
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Setter
    private String description;

    @Column(nullable = false)
    @Setter
    private int maxParticipants;

    @Column(nullable = false)
    @Setter
    private int currentParticipants;

    @Column(nullable = false)
    @Setter
    private int waitingListCount;

    @Column(nullable = false)
    @Setter
    private UUID instructorId;

    @Column(nullable = false)
    @Setter
    private String instructorName;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime startDate;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime endDate;

    @Column(nullable = false)
    @Setter
    private String status;

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    @Setter
    private OffsetDateTime updatedAt;

    protected Course() {
        // JPA only
    }

    public Course(
            String name,
            String description,
            int maxParticipants,
            UUID instructorId,
            String instructorName,
            OffsetDateTime startDate,
            OffsetDateTime endDate,
            String status
    ) {
        Assert.hasText(name, "name must not be empty");
        if (maxParticipants < 1) {
            throw new IllegalArgumentException("maxParticipants must be positive");
        }
        if (instructorId == null) {
            throw new IllegalArgumentException("instructorId must not be null");
        }
        if (startDate == null) {
            throw new IllegalArgumentException("startDate must not be null");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("endDate must not be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status must not be null");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.maxParticipants = maxParticipants;
        this.currentParticipants = 0;
        this.waitingListCount = 0;
        this.instructorId = instructorId;
        this.instructorName = instructorName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
        this.createdAt = OffsetDateTime.now();
        this.updatedAt = OffsetDateTime.now();
    }

    public Course(
            String name,
            String description,
            int maxParticipants,
            UUID instructorId,
            String instructorName,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    ) {
        this(
                name,
                description,
                maxParticipants,
                instructorId,
                instructorName,
                startDate,
                endDate,
                "PLANNED"
        );
    }

    public void incrementCurrentParticipants() {
        this.currentParticipants++;
        this.updatedAt = OffsetDateTime.now();
    }

    public void incrementWaitingListCount() {
        this.waitingListCount++;
        this.updatedAt = OffsetDateTime.now();
    }

    public void decrementCurrentParticipants() {
        this.currentParticipants--;
        this.updatedAt = OffsetDateTime.now();
    }

    public void decrementWaitingListCount() {
        this.waitingListCount--;
        this.updatedAt = OffsetDateTime.now();
    }
}