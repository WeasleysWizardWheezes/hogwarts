package de.thkoeln.ccq.firemanager.member;

import de.thkoeln.ccq.firemanager.location.Location;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.UUID;

@Entity
@Table(name = "member_location_assignments")
@Getter
public class MemberLocationAssignment {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, name = "member_id")
    @Setter
    private UUID memberId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Setter
    private Location location;

    protected MemberLocationAssignment() {
        // JPA only
    }

    public MemberLocationAssignment(UUID memberId, Location location) {
        if (memberId == null) {
            throw new IllegalArgumentException("memberId must not be null");
        }
        if (location == null) {
            throw new IllegalArgumentException("location must not be null");
        }

        this.id = UUID.randomUUID();
        this.memberId = memberId;
        this.location = location;
    }
}