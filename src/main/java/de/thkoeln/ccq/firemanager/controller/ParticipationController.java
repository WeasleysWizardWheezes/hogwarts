package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.service.ParticipationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments/{appointmentId}/participants")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping
    public ResponseEntity<Void> addParticipants(@PathVariable String appointmentId, @RequestBody List<String> userIds) {
        participationService.addParticipants(appointmentId, userIds);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeParticipants(@PathVariable String appointmentId, @RequestBody List<String> userIds) {
        participationService.removeParticipants(appointmentId, userIds);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/accept")
    public ResponseEntity<Void> acceptParticipation(@PathVariable String appointmentId, @PathVariable String userId) {
        participationService.acceptParticipation(appointmentId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/decline")
    public ResponseEntity<Void> declineParticipation(@PathVariable String appointmentId, @PathVariable String userId) {
        participationService.declineParticipation(appointmentId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{userId}/status")
    public ResponseEntity<String> getParticipationStatus(@PathVariable String appointmentId, @PathVariable String userId) {
        String status = participationService.getParticipationStatus(appointmentId, userId);
        return ResponseEntity.ok(status);
    }
}
