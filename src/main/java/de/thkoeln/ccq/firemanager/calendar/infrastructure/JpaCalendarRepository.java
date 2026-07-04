package de.thkoeln.ccq.firemanager.calendar.infrastructure;

import de.thkoeln.ccq.firemanager.calendar.domain.Calendar;
import de.thkoeln.ccq.firemanager.calendar.domain.CalendarId;
import de.thkoeln.ccq.firemanager.calendar.domain.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class JpaCalendarRepository implements CalendarRepository {
    private final SpringDataCalendarRepository springDataCalendarRepository;
    
    @Override
    public Optional<Calendar> findById(CalendarId id) {
        return springDataCalendarRepository.findById(id.value())
                .map(JpaCalendarMapper::toDomain);
    }
    
    @Override
    public Calendar save(Calendar calendar) {
        var entity = JpaCalendarMapper.toEntity(calendar);
        var savedEntity = springDataCalendarRepository.save(entity);
        return JpaCalendarMapper.toDomain(savedEntity);
    }
    
    @Override
    public void deleteById(CalendarId id) {
        springDataCalendarRepository.deleteById(id.value());
    }
    
    @Override
    public Iterable<Calendar> findAll() {
        return () -> springDataCalendarRepository.findAll().stream()
                .map(JpaCalendarMapper::toDomain)
                .iterator();
    }
}