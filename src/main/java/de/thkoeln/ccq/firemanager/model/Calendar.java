package de.thkoeln.ccq.firemanager.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Calendar {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;
    private boolean isPublic;

    @ManyToOne
    @JoinColumn(name = "organization_unit_id")
    private OrganizationUnit organizationUnit;

    @OneToMany(mappedBy = "calendar")
    private Set<Appointment> appointments;

    @OneToMany(mappedBy = "calendar")
    private Set<Alarm> alarms;

    @ManyToMany
    @JoinTable(
        name = "calendar_user",
        joinColumns = @JoinColumn(name = "calendar_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> sharedWithUsers;
}
