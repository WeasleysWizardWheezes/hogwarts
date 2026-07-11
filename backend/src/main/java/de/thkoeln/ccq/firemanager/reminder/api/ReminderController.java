package de.thkoeln.ccq.firemanager.reminder.api;

import de.thkoeln.ccq.firemanager.reminder.application.ReminderApplicationService;
import de.thkoeln.ccq.firemanager.reminder.domain.Channel;
import de.thkoeln.ccq.firemanager.reminder.domain.Reminder;
import de.thkoeln.ccq.firemanager.reminder.domain.ReminderId;
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
    
    private final ReminderApplicationService reminderApplicationService;
    private final ReminderApiMapper mapper;
    
    @GetMapping
    public ResponseEntity<List<ReminderResponse>> getAllReminders(
        @RequestParam(required = false) UUID userId,
        @RequestParam(required = false) Channel channel
    ) {
        List<Reminder> reminders;
        
        if (userId != null) {
            reminders = reminderApplicationService.getRemindersByUserId(userId);
        } else if (channel != null) {
            reminders = reminderApplicationService.getRemindersByChannel(channel);
        } else {
            reminders = reminderApplicationService.getAllReminders();
        }
        
        var response = reminders.stream()
            .map(mapper::toResponse)
            .toList();
        
        return ResponseEntity.ok(response);
    }
    
    @PatchMapping("/{reminderId}")
    public ResponseEntity<ReminderResponse> updateReminder(
        @PathVariable UUID reminderId,
        @Valid @RequestBody ReminderUpdateRequest request
    ) {
        ReminderId reminderDomainId = ReminderId.from(reminderId);
        Reminder updatedReminder = reminderApplicationService.updateReminder(
            reminderDomainId,
            request.leadTimeMinutes(),
            request.channel()
        );
        
        return ResponseEntity.ok(mapper.toResponse(updatedReminder));
    }
}