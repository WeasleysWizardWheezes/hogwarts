package com.hogwarts.reminder;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "event_id", nullable = false)
    private UUID eventId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private String title;

    @Column
    private String description;

    @Column(name = "reminder_time", nullable = false)
    private LocalDateTime reminderTime;

    @Column(name = "lead_time_minutes", nullable = false)
    private int leadTimeMinutes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReminderChannel channel;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum EventType {
        APPOINTMENT, OPERATION
    }

    public enum ReminderChannel {
        EMAIL, PUSH
    }
}
