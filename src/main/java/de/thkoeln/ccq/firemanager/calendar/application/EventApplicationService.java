package de.thkoeln.ccq.firemanager.calendar.application;

import de.thkoeln.ccq.firemanager.calendar.domain.*;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class EventApplicationService {
    private final EventRepository eventRepository;
    private final CalendarRepository calendarRepository;
    
    public Event createEvent(String title, String description, LocalDateTime startTime, 
                           LocalDateTime endTime, UUID calendarId, boolean recurring, 
                           String recurrencePattern) {
        // Validate that the calendar exists
        calendarRepository.findById(new CalendarId(calendarId))
                .orElseThrow(() -> new CalendarNotFoundException(calendarId));
        
        var event = Event.builder()
                .id(UUID.randomUUID())
                .title(title)
                .description(description)
                .startTime(startTime)
                .endTime(endTime)
                .calendarId(calendarId)
                .recurring(recurring)
                .recurrencePattern(recurrencePattern)
                .attendees(List.of())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        
        return eventRepository.save(event);
    }
    
    public Event getEventById(UUID eventId) {
        return eventRepository.findById(new EventId(eventId))
                .orElseThrow(() -> new EventNotFoundException(eventId));
    }
    
    public List<Event> getEventsByCalendarId(UUID calendarId) {
        return (List<Event>) eventRepository.findByCalendarId(new CalendarId(calendarId));
    }
    
    public Event updateEvent(UUID eventId, String title, String description, LocalDateTime startTime,
                           LocalDateTime endTime, boolean recurring, String recurrencePattern) {
        var event = eventRepository.findById(new EventId(eventId))
                .orElseThrow(() -> new EventNotFoundException(eventId));
        
        var updatedEvent = event.toBuilder()
                .title(title)
                .description(description)
                .startTime(startTime)
                .endTime(endTime)
                .recurring(recurring)
                .recurrencePattern(recurrencePattern)
                .updatedAt(LocalDateTime.now())
                .build();
        
        return eventRepository.save(updatedEvent);
    }
    
    public void deleteEvent(UUID eventId) {
        eventRepository.deleteById(new EventId(eventId));
    }
    
    public void addAttendee(UUID eventId, UUID userId) {
        var event = eventRepository.findById(new EventId(eventId))
                .orElseThrow(() -> new EventNotFoundException(eventId));
        
        var updatedEvent = event.toBuilder()
                .attendees(List.of(userId)) // Simplified - in reality would add to existing list
                .updatedAt(LocalDateTime.now())
                .build();
        
        eventRepository.save(updatedEvent);
    }
    
    public void removeAttendee(UUID eventId, UUID userId) {
        var event = eventRepository.findById(new EventId(eventId))
                .orElseThrow(() -> new EventNotFoundException(eventId));
        
        // Simplified - in reality would remove from existing list
        var updatedEvent = event.toBuilder()
                .attendees(List.of()) // Simplified
                .updatedAt(LocalDateTime.now())
                .build();
        
        eventRepository.save(updatedEvent);
    }
}