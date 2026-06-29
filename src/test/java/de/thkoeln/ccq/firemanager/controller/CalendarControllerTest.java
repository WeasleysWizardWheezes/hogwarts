package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.CalendarDTO;
import de.thkoeln.ccq.firemanager.service.CalendarService;
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
class CalendarControllerTest {

    @Mock
    private CalendarService calendarService;

    @InjectMocks
    private CalendarController calendarController;

    private CalendarDTO calendarDTO;

    @BeforeEach
    void setUp() {
        calendarDTO = CalendarDTO.builder()
                .id("1")
                .name("Test Calendar")
                .description("Test Description")
                .isPublic(true)
                .organizationUnitId("org1")
                .build();
    }

    @Test
    void createCalendar() {
        when(calendarService.createCalendar(any(CalendarDTO.class))).thenReturn(calendarDTO);

        ResponseEntity<CalendarDTO> response = calendarController.createCalendar(calendarDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(calendarDTO, response.getBody());
    }

    @Test
    void getCalendarById() {
        when(calendarService.getCalendarById(anyString())).thenReturn(calendarDTO);

        ResponseEntity<CalendarDTO> response = calendarController.getCalendarById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(calendarDTO, response.getBody());
    }

    @Test
    void getAllCalendars() {
        List<CalendarDTO> calendars = Arrays.asList(calendarDTO);
        when(calendarService.getAllCalendars()).thenReturn(calendars);

        ResponseEntity<List<CalendarDTO>> response = calendarController.getAllCalendars();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getCalendarsByOrganizationUnit() {
        List<CalendarDTO> calendars = Arrays.asList(calendarDTO);
        when(calendarService.getCalendarsByOrganizationUnit(anyString())).thenReturn(calendars);

        ResponseEntity<List<CalendarDTO>> response = calendarController.getCalendarsByOrganizationUnit("org1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateCalendar() {
        when(calendarService.updateCalendar(anyString(), any(CalendarDTO.class))).thenReturn(calendarDTO);

        ResponseEntity<CalendarDTO> response = calendarController.updateCalendar("1", calendarDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(calendarDTO, response.getBody());
    }

    @Test
    void deleteCalendar() {
        doNothing().when(calendarService).deleteCalendar(anyString());

        ResponseEntity<Void> response = calendarController.deleteCalendar("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
