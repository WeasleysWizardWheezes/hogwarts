package de.thkoeln.ccq.firemanager.calendar.application;

import de.thkoeln.ccq.firemanager.calendar.domain.Calendar;
import de.thkoeln.ccq.firemanager.calendar.domain.CalendarId;
import de.thkoeln.ccq.firemanager.calendar.domain.CalendarRepository;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class CalendarApplicationService {
    private final CalendarRepository calendarRepository;
    
    public List<Calendar> getAllCalendars() {
        return (List<Calendar>) calendarRepository.findAll();
    }
    
    public Calendar createCalendar(String name, String description) {
        var calendar = Calendar.builder()
                .id(UUID.randomUUID())
                .name(name)
                .description(description)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();
        
        return calendarRepository.save(calendar);
    }
    
    public Calendar getCalendarById(UUID calendarId) {
        return calendarRepository.findById(new CalendarId(calendarId))
                .orElseThrow(() -> new CalendarNotFoundException(calendarId));
    }
    
    public void deleteCalendar(UUID calendarId) {
        calendarRepository.deleteById(new CalendarId(calendarId));
    }
}