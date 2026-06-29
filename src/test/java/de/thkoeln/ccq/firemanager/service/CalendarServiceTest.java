package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.CalendarDTO;
import de.thkoeln.ccq.firemanager.model.Calendar;
import de.thkoeln.ccq.firemanager.repository.CalendarRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarServiceTest {

    @Mock
    private CalendarRepository calendarRepository;

    @InjectMocks
    private CalendarService calendarService;

    private CalendarDTO calendarDTO;
    private Calendar calendar;

    @BeforeEach
    void setUp() {
        calendarDTO = CalendarDTO.builder()
                .id("1")
                .name("Test Calendar")
                .description("Test Description")
                .isPublic(true)
                .organizationUnitId("org1")
                .build();

        calendar = Calendar.builder()
                .id("1")
                .name("Test Calendar")
                .description("Test Description")
                .isPublic(true)
                .build();
    }

    @Test
    void createCalendar() {
        when(calendarRepository.save(any(Calendar.class))).thenReturn(calendar);

        CalendarDTO result = calendarService.createCalendar(calendarDTO);

        assertNotNull(result);
        assertEquals(calendarDTO.getId(), result.getId());
        assertEquals(calendarDTO.getName(), result.getName());
        assertEquals(calendarDTO.isPublic(), result.isPublic());
    }

    @Test
    void getCalendarById() {
        when(calendarRepository.findById("1")).thenReturn(Optional.of(calendar));

        CalendarDTO result = calendarService.getCalendarById("1");

        assertNotNull(result);
        assertEquals(calendar.getId(), result.getId());
        assertEquals(calendar.getName(), result.getName());
    }

    @Test
    void getCalendarById_NotFound() {
        when(calendarRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            calendarService.getCalendarById("1");
        });
    }

    @Test
    void getAllCalendars() {
        List<Calendar> calendars = Arrays.asList(calendar);
        when(calendarRepository.findAll()).thenReturn(calendars);

        List<CalendarDTO> result = calendarService.getAllCalendars();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(calendar.getId(), result.get(0).getId());
    }

    @Test
    void getCalendarsByOrganizationUnit() {
        List<Calendar> calendars = Arrays.asList(calendar);
        when(calendarRepository.findByOrganizationUnitId("org1")).thenReturn(calendars);

        List<CalendarDTO> result = calendarService.getCalendarsByOrganizationUnit("org1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(calendar.getId(), result.get(0).getId());
    }

    @Test
    void updateCalendar() {
        when(calendarRepository.findById("1")).thenReturn(Optional.of(calendar));
        when(calendarRepository.save(any(Calendar.class))).thenReturn(calendar);

        CalendarDTO result = calendarService.updateCalendar("1", calendarDTO);

        assertNotNull(result);
        assertEquals(calendarDTO.getId(), result.getId());
        assertEquals(calendarDTO.getName(), result.getName());
    }

    @Test
    void updateCalendar_NotFound() {
        when(calendarRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            calendarService.updateCalendar("1", calendarDTO);
        });
    }

    @Test
    void deleteCalendar() {
        doNothing().when(calendarRepository).deleteById("1");

        assertDoesNotThrow(() -> {
            calendarService.deleteCalendar("1");
        });

        verify(calendarRepository, times(1)).deleteById("1");
    }
}
