package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.ReminderDTO;
import de.thkoeln.ccq.firemanager.service.ReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @PostMapping
    public ResponseEntity<ReminderDTO> createReminder(@RequestBody ReminderDTO reminderDTO) {
        ReminderDTO createdReminder = reminderService.createReminder(reminderDTO);
        return new ResponseEntity<>(createdReminder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReminderDTO> getReminderById(@PathVariable String id) {
        ReminderDTO reminderDTO = reminderService.getReminderById(id);
        return ResponseEntity.ok(reminderDTO);
    }

    @GetMapping
    public ResponseEntity<List<ReminderDTO>> getAllReminders() {
        List<ReminderDTO> reminders = reminderService.getAllReminders();
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<List<ReminderDTO>> getRemindersByAppointment(@PathVariable String appointmentId) {
        List<ReminderDTO> reminders = reminderService.getRemindersByAppointment(appointmentId);
        return ResponseEntity.ok(reminders);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReminderDTO>> getRemindersByUser(@PathVariable String userId) {
        List<ReminderDTO> reminders = reminderService.getRemindersByUser(userId);
        return ResponseEntity.ok(reminders);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReminderDTO> updateReminder(@PathVariable String id, @RequestBody ReminderDTO reminderDTO) {
        ReminderDTO updatedReminder = reminderService.updateReminder(id, reminderDTO);
        return ResponseEntity.ok(updatedReminder);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReminder(@PathVariable String id) {
        reminderService.deleteReminder(id);
        return ResponseEntity.noContent().build();
    }
}
