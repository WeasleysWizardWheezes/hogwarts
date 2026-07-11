package de.thkoeln.ccq.firemanager.reminder.api;

import de.thkoeln.ccq.firemanager.reminder.domain.Channel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReminderUpdateRequest(
    @NotNull
    @Min(value = 1, message = "Lead time must be at least 1 minute")
    Integer leadTimeMinutes,
    
    @NotNull
    Channel channel
) {}