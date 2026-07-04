package de.thkoeln.ccq.firemanager.calendar.infrastructure;

import de.thkoeln.ccq.firemanager.calendar.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class JpaEventRepository implements EventRepository {
    private final SpringDataEventRepository springDataEventRepository;
    
    @Override
    public Optional<Event> findById(EventId id) {
        return springDataEventRepository.findById(id.value())
                .map(JpaEventMapper::toDomain);
    }
    
    @Override
    public Event save(Event event) {
        var entity = JpaEventMapper.toEntity(event);
        var savedEntity = springDataEventRepository.save(entity);
        return JpaEventMapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(EventId id) {
        springDataEventRepository.deleteById(id.value());
    }
    
    @Override
    public Iterable<Event> findAll() {
        return () -> springDataEventRepository.findAll().stream()
                .map(JpaEventMapper::toDomain)
                .iterator();
    }
    
    @Override
    public Iterable<Event> findByCalendarId(CalendarId calendarId) {
        return () -> springDataEventRepository.findByCalendarId(calendarId.value()).stream()
                .map(JpaEventMapper::toDomain)
                .iterator();
    }
}