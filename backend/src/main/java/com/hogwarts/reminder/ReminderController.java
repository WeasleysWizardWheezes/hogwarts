package com.hogwarts.reminder;

import com.hogwarts.reminder.dto.ReminderDto;
import com.hogwarts.reminder.dto.ReminderUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reminders")
@RequiredArgsConstructor
public class ReminderController {

    private final ReminderService reminderService;

    @GetMapping
    public ResponseEntity<List<ReminderDto>> getAllReminders(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Reminder.ReminderChannel channel) {
        List<ReminderDto> reminders = reminderService.getAllReminders(userId, channel);
        return ResponseEntity.ok(reminders);
    }

    @PatchMapping("/{reminderId}")
    public ResponseEntity<ReminderDto> updateReminder(
            @PathVariable UUID reminderId,
            @Valid @RequestBody ReminderUpdateDto updateDto) {
        ReminderDto updatedReminder = reminderService.updateReminder(reminderId, updateDto);
        return ResponseEntity.ok(updatedReminder);
    }
}
