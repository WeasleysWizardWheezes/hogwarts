package de.thkoeln.ccq.firemanager.document;

import de.thkoeln.ccq.firemanager.member.MemberLocationAssignment;
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

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "documents")
@Getter
public class Document {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    @Setter
    private String originalName;

    @Column(nullable = false)
    @Setter
    private String mimeType;

    @Column(nullable = false)
    @Setter
    private Long size;

    @Column(nullable = false)
    @Setter
    private String storagePath;

    @Column(nullable = false)
    @Setter
    private UUID uploadedBy;

    @Column(nullable = false)
    @Setter
    private LocalDateTime uploadedAt;

    @Column(nullable = false, name = "member_id")
    @Setter
    private UUID memberId;

    protected Document() {
        // JPA only
    }

    public Document(String originalName, String mimeType, Long size, String storagePath,
            UUID uploadedBy, LocalDateTime uploadedAt, UUID memberId) {
        Assert.hasText(originalName, "originalName must not be empty");
        Assert.hasText(mimeType, "mimeType must not be empty");
        if (size == null || size <= 0) {
            throw new IllegalArgumentException("size must be positive");
        }
        Assert.hasText(storagePath, "storagePath must not be empty");
        if (uploadedBy == null) {
            throw new IllegalArgumentException("uploadedBy must not be null");
        }
        if (uploadedAt == null) {
            throw new IllegalArgumentException("uploadedAt must not be null");
        }
        if (memberId == null) {
            throw new IllegalArgumentException("memberId must not be null");
        }

        this.id = UUID.randomUUID();
        this.originalName = originalName;
        this.mimeType = mimeType;
        this.size = size;
        this.storagePath = storagePath;
        this.uploadedBy = uploadedBy;
        this.uploadedAt = uploadedAt;
        this.memberId = memberId;
    }
}