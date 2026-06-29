package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.CalendarDTO;
import de.thkoeln.ccq.firemanager.service.CalendarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendars")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    @PostMapping
    public ResponseEntity<CalendarDTO> createCalendar(@RequestBody CalendarDTO calendarDTO) {
        CalendarDTO createdCalendar = calendarService.createCalendar(calendarDTO);
        return new ResponseEntity<>(createdCalendar, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendarDTO> getCalendarById(@PathVariable String id) {
        CalendarDTO calendarDTO = calendarService.getCalendarById(id);
        return ResponseEntity.ok(calendarDTO);
    }

    @GetMapping
    public ResponseEntity<List<CalendarDTO>> getAllCalendars() {
        List<CalendarDTO> calendars = calendarService.getAllCalendars();
        return ResponseEntity.ok(calendars);
    }

    @GetMapping("/organization-unit/{organizationUnitId}")
    public ResponseEntity<List<CalendarDTO>> getCalendarsByOrganizationUnit(@PathVariable String organizationUnitId) {
        List<CalendarDTO> calendars = calendarService.getCalendarsByOrganizationUnit(organizationUnitId);
        return ResponseEntity.ok(calendars);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CalendarDTO> updateCalendar(@PathVariable String id, @RequestBody CalendarDTO calendarDTO) {
        CalendarDTO updatedCalendar = calendarService.updateCalendar(id, calendarDTO);
        return ResponseEntity.ok(updatedCalendar);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable String id) {
        calendarService.deleteCalendar(id);
        return ResponseEntity.noContent().build();
    }
}
