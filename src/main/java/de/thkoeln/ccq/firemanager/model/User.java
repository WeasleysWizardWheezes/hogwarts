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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String username;
    private String email;
    private String firstName;
    private String lastName;

    @ManyToMany(mappedBy = "sharedWithUsers")
    private Set<Calendar> sharedCalendars;

    @ManyToMany(mappedBy = "participants")
    private Set<Appointment> appointments;

    @OneToMany(mappedBy = "user")
    private Set<Reminder> reminders;
}
