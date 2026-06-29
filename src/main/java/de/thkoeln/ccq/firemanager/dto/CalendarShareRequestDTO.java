package de.thkoeln.ccq.firemanager.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarShareRequestDTO {
    private List<String> userIds;
    private List<String> permissions;
}
