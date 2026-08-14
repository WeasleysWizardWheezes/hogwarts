package de.thkoeln.ccq.firemanager.course;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.JoinColumn;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "courses")
@Getter
public class Course {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, unique = true)
    @Setter
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    @Setter
    private String description;

    @Column(nullable = false)
    @Setter
    private CourseCategory category;

    @ElementCollection
    @CollectionTable(name = "course_prerequisites", joinColumns = @JoinColumn(name = "course_id"))
    @Column(name = "prerequisite_id")
    @Setter
    private List<UUID> prerequisites = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;

    protected Course() {
        // JPA only
    }

    public Course(String name, String description, CourseCategory category, List<UUID> prerequisites) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(description, "description must not be empty");
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.category = category;
        this.prerequisites = prerequisites != null ? new ArrayList<>(prerequisites) : new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Course(String name, String description, CourseCategory category) {
        this(name, description, category, new ArrayList<>());
    }

    public void update(String name, String description, CourseCategory category, List<UUID> prerequisites) {
        Assert.hasText(name, "name must not be empty");
        Assert.hasText(description, "description must not be empty");
        if (category == null) {
            throw new IllegalArgumentException("category must not be null");
        }

        this.name = name;
        this.description = description;
        this.category = category;
        this.prerequisites = prerequisites != null ? new ArrayList<>(prerequisites) : new ArrayList<>();
        this.updatedAt = LocalDateTime.now();
    }

    public enum CourseCategory {
        AGT,
        MASCHINIST,
        TRUPPMANN,
        TRUPPFUEHRER,
        GRUPPENFUEHRER,
        ZUGFUEHRER,
        LDF,
        ABC,
        MEDIZIN,
        SONSTIGE
    }
}
