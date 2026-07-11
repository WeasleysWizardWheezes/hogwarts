package de.thkoeln.ccq.firemanager.reminder.infrastructure;

import de.thkoeln.ccq.firemanager.reminder.domain.Channel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataReminderRepository extends JpaRepository<ReminderJpaEntity, UUID> {
    
    List<ReminderJpaEntity> findByChannel(Channel channel);
}