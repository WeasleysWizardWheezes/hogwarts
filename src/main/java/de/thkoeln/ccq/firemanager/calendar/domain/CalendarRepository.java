package de.thkoeln.ccq.firemanager.calendar.domain;

import java.util.Optional;
import java.util.UUID;

public interface CalendarRepository {
    Optional<Calendar> findById(CalendarId id);
    Calendar save(Calendar calendar);
    void deleteById(CalendarId id);
    Iterable<Calendar> findAll();
}