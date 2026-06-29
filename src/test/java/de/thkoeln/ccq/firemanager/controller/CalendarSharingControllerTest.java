package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.CalendarShareRequestDTO;
import de.thkoeln.ccq.firemanager.service.CalendarSharingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarSharingControllerTest {

    @Mock
    private CalendarSharingService calendarSharingService;

    @InjectMocks
    private CalendarSharingController calendarSharingController;

    private CalendarShareRequestDTO shareRequestDTO;

    @BeforeEach
    void setUp() {
        shareRequestDTO = CalendarShareRequestDTO.builder()
                .userIds(Arrays.asList("user1", "user2"))
                .permissions(Arrays.asList("READ", "WRITE"))
                .build();
    }

    @Test
    void shareCalendar() {
        doNothing().when(calendarSharingService).shareCalendar(anyString(), any(List.class), any(List.class));

        ResponseEntity<Void> response = calendarSharingController.shareCalendar("cal1", shareRequestDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}
