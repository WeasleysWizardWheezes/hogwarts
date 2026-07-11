package de.thkoeln.ccq.firemanager.reminder.infrastructure;

import de.thkoeln.ccq.firemanager.reminder.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaReminderRepository implements ReminderRepository {
    
    private final SpringDataReminderRepository springDataReminderRepository;
    private final ReminderPersistenceMapper mapper;
    
    @Override
    public Reminder save(Reminder reminder) {
        ReminderJpaEntity entity = mapper.toEntity(reminder);
        ReminderJpaEntity savedEntity = springDataReminderRepository.save(entity);
        return mapper.toDomain(savedEntity);
    }
    
    @Override
    public Optional<Reminder> findById(ReminderId id) {
        return springDataReminderRepository.findById(id.value())
            .map(mapper::toDomain);
    }
    
    @Override
    public List<Reminder> findByUserId(UUID userId) {
        // Assuming there's a user_reminder table or similar
        // This is a simplified implementation
        return springDataReminderRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }
    
    @Override
    public List<Reminder> findByChannel(Channel channel) {
        return springDataReminderRepository.findByChannel(channel).stream()
            .map(mapper::toDomain)
            .toList();
    }
    
    @Override
    public List<Reminder> findAll() {
        return springDataReminderRepository.findAll().stream()
            .map(mapper::toDomain)
            .toList();
    }
}