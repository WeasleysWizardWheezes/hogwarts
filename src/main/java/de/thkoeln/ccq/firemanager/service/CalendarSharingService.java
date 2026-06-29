package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.model.Calendar;
import de.thkoeln.ccq.firemanager.model.User;
import de.thkoeln.ccq.firemanager.repository.CalendarRepository;
import de.thkoeln.ccq.firemanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarSharingService {

    private final CalendarRepository calendarRepository;
    private final UserRepository userRepository;

    public void shareCalendar(String calendarId, List<String> userIds, List<String> permissions) {
        Calendar calendar = calendarRepository.findById(calendarId)
                .orElseThrow(() -> new RuntimeException("Calendar not found"));
        
        Set<User> usersToShareWith = userIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new RuntimeException("User not found: " + userId)))
                .collect(Collectors.toSet());
        
        calendar.setSharedWithUsers(usersToShareWith);
        calendarRepository.save(calendar);
        
        // TODO: Implement permission handling logic
    }
}
