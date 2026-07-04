package de.thkoeln.ccq.firemanager.calendar.domain;

import java.util.Optional;
import java.util.UUID;

public interface EventRepository {
    Optional<Event> findById(EventId id);
    Event save(Event event);
    void deleteById(EventId id);
    Iterable<Event> findAll();
    Iterable<Event> findByCalendarId(CalendarId calendarId);
}