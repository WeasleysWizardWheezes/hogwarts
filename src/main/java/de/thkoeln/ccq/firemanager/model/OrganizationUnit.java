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
public class OrganizationUnit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String name;
    private String description;

    @OneToMany(mappedBy = "organizationUnit")
    private Set<Calendar> calendars;

    @OneToMany(mappedBy = "organizationUnit")
    private Set<Service> services;

    @OneToMany(mappedBy = "organizationUnit")
    private Set<Exercise> exercises;

    @OneToMany(mappedBy = "organizationUnit")
    private Set<Appointment> appointments;

    @OneToMany(mappedBy = "organizationUnit")
    private Set<Alarm> alarms;
}
