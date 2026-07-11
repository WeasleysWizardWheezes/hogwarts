package de.thkoeln.ccq.firemanager.reminder.infrastructure;

import de.thkoeln.ccq.firemanager.reminder.domain.Channel;
import de.thkoeln.ccq.firemanager.reminder.domain.EventType;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "reminders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderJpaEntity {
    
    @Id
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
    private ZonedDateTime reminderTime;
    
    @Column(name = "lead_time_minutes", nullable = false)
    private int leadTimeMinutes;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Channel channel;
    
    @Column(name = "created_at", nullable = false)
    private ZonedDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;
}