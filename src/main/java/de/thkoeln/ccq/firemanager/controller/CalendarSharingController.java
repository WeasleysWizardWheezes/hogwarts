package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.CalendarShareRequestDTO;
import de.thkoeln.ccq.firemanager.service.CalendarSharingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/calendars/{calendarId}/share")
@RequiredArgsConstructor
public class CalendarSharingController {

    private final CalendarSharingService calendarSharingService;

    @PostMapping
    public ResponseEntity<Void> shareCalendar(@PathVariable String calendarId, @RequestBody CalendarShareRequestDTO request) {
        calendarSharingService.shareCalendar(calendarId, request.getUserIds(), request.getPermissions());
        return ResponseEntity.ok().build();
    }
}
