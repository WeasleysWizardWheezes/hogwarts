package de.thkoeln.ccq.firemanager.calendar.interfaces;

import de.thkoeln.ccq.firemanager.calendar.application.CalendarApplicationService;
import de.thkoeln.ccq.firemanager.calendar.application.EventApplicationService;
import de.thkoeln.ccq.firemanager.calendar.domain.Calendar;
import de.thkoeln.ccq.firemanager.calendar.domain.Event;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/calendars")
@RequiredArgsConstructor
public class CalendarEventController {
    
    private final CalendarApplicationService calendarApplicationService;
    private final EventApplicationService eventApplicationService;
    
    // Calendar endpoints
    
    @GetMapping
    public ResponseEntity<List<Calendar>> getAllCalendars() {
        var calendars = calendarApplicationService.getAllCalendars();
        return ResponseEntity.ok(calendars);
    }
    
    @GetMapping("/{calendarId}/events")
    public ResponseEntity<List<Event>> getEventsByCalendarId(@PathVariable UUID calendarId) {
        var events = eventApplicationService.getEventsByCalendarId(calendarId);
        return ResponseEntity.ok(events);
    }
    
    // Event endpoints
    
    @PostMapping
    public ResponseEntity<Event> createEvent(
            @Valid @RequestBody CreateEventRequest request) {
        var event = eventApplicationService.createEvent(
                request.title(),
                request.description(),
                request.startTime(),
                request.endTime(),
                request.calendarId(),
                request.recurring(),
                request.recurrencePattern()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(event);
    }
    
    @PutMapping
    public ResponseEntity<Event> updateEvent(
            @Valid @RequestBody UpdateEventRequest request) {
        var event = eventApplicationService.updateEvent(
                request.eventId(),
                request.title(),
                request.description(),
                request.startTime(),
                request.endTime(),
                request.recurring(),
                request.recurrencePattern()
        );
        return ResponseEntity.ok(event);
    }
    
    @DeleteMapping
    public ResponseEntity<Void> deleteEvent(@RequestParam UUID eventId) {
        eventApplicationService.deleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }
    
    // Attendance endpoints
    
    @PostMapping("/{eventId}/attendees")
    public ResponseEntity<Void> addAttendee(
            @PathVariable UUID eventId,
            @Valid @RequestBody AddAttendeeRequest request) {
        eventApplicationService.addAttendee(eventId, request.userId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @DeleteMapping("/{eventId}/attendees")
    public ResponseEntity<Void> removeAttendee(
            @PathVariable UUID eventId,
            @RequestParam UUID userId) {
        eventApplicationService.removeAttendee(eventId, userId);
        return ResponseEntity.noContent().build();
    }
    
    // Request DTOs
    
    public record CreateEventRequest(
            String title,
            String description,
            LocalDateTime startTime,
            LocalDateTime endTime,
            UUID calendarId,
            boolean recurring,
            String recurrencePattern
    ) {}
    
    public record UpdateEventRequest(
            UUID eventId,
            String title,
            String description,
            LocalDateTime startTime,
            LocalDateTime endTime,
            boolean recurring,
            String recurrencePattern
    ) {}
    
    public record AddAttendeeRequest(
            UUID userId
    ) {}
}