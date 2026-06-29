package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.model.Calendar;
import de.thkoeln.ccq.firemanager.model.User;
import de.thkoeln.ccq.firemanager.repository.CalendarRepository;
import de.thkoeln.ccq.firemanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarSharingServiceTest {

    @Mock
    private CalendarRepository calendarRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CalendarSharingService calendarSharingService;

    private Calendar calendar;
    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
        calendar = Calendar.builder()
                .id("cal1")
                .name("Test Calendar")
                .description("Test Description")
                .isPublic(false)
                .sharedWithUsers(new HashSet<>())
                .build();

        user1 = User.builder()
                .id("user1")
                .username("user1")
                .email("user1@example.com")
                .firstName("User")
                .lastName("One")
                .build();

        user2 = User.builder()
                .id("user2")
                .username("user2")
                .email("user2@example.com")
                .firstName("User")
                .lastName("Two")
                .build();
    }

    @Test
    void shareCalendar() {
        List<String> userIds = Arrays.asList("user1", "user2");
        List<String> permissions = Arrays.asList("READ", "WRITE");

        when(calendarRepository.findById("cal1")).thenReturn(Optional.of(calendar));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user2")).thenReturn(Optional.of(user2));
        when(calendarRepository.save(any(Calendar.class))).thenReturn(calendar);

        assertDoesNotThrow(() -> {
            calendarSharingService.shareCalendar("cal1", userIds, permissions);
        });

        verify(calendarRepository, times(1)).findById("cal1");
        verify(userRepository, times(1)).findById("user1");
        verify(userRepository, times(1)).findById("user2");
        verify(calendarRepository, times(1)).save(any(Calendar.class));
    }

    @Test
    void shareCalendar_CalendarNotFound() {
        List<String> userIds = Arrays.asList("user1", "user2");
        List<String> permissions = Arrays.asList("READ", "WRITE");

        when(calendarRepository.findById("cal1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            calendarSharingService.shareCalendar("cal1", userIds, permissions);
        });

        verify(calendarRepository, times(1)).findById("cal1");
        verify(userRepository, never()).findById(anyString());
        verify(calendarRepository, never()).save(any(Calendar.class));
    }

    @Test
    void shareCalendar_UserNotFound() {
        List<String> userIds = Arrays.asList("user1", "user2");
        List<String> permissions = Arrays.asList("READ", "WRITE");

        when(calendarRepository.findById("cal1")).thenReturn(Optional.of(calendar));
        when(userRepository.findById("user1")).thenReturn(Optional.of(user1));
        when(userRepository.findById("user2")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            calendarSharingService.shareCalendar("cal1", userIds, permissions);
        });

        verify(calendarRepository, times(1)).findById("cal1");
        verify(userRepository, times(1)).findById("user1");
        verify(userRepository, times(1)).findById("user2");
        verify(calendarRepository, never()).save(any(Calendar.class));
    }
}
