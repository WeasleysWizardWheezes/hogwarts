package com.hogwarts.reminder.dto;

import com.hogwarts.reminder.Reminder;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReminderUpdateDto {
    @NotNull(message = "Lead time minutes cannot be null")
    @Min(value = 1, message = "Lead time minutes must be at least 1")
    private int leadTimeMinutes;

    @NotNull(message = "Channel cannot be null")
    private Reminder.ReminderChannel channel;
}
