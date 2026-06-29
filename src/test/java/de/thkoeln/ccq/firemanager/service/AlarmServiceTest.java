package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.AlarmDTO;
import de.thkoeln.ccq.firemanager.model.Alarm;
import de.thkoeln.ccq.firemanager.repository.AlarmRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmServiceTest {

    @Mock
    private AlarmRepository alarmRepository;

    @InjectMocks
    private AlarmService alarmService;

    private AlarmDTO alarmDTO;
    private Alarm alarm;

    @BeforeEach
    void setUp() {
        alarmDTO = AlarmDTO.builder()
                .id("1")
                .title("Test Alarm")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .organizationUnitId("org1")
                .calendarId("cal1")
                .build();

        alarm = Alarm.builder()
                .id("1")
                .title("Test Alarm")
                .description("Test Description")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusHours(2))
                .isRecurring(false)
                .recurrencePattern("DAILY")
                .build();
    }

    @Test
    void createAlarm() {
        when(alarmRepository.save(any(Alarm.class))).thenReturn(alarm);

        AlarmDTO result = alarmService.createAlarm(alarmDTO);

        assertNotNull(result);
        assertEquals(alarmDTO.getId(), result.getId());
        assertEquals(alarmDTO.getTitle(), result.getTitle());
        assertEquals(alarmDTO.getStartDate(), result.getStartDate());
    }

    @Test
    void getAlarmById() {
        when(alarmRepository.findById("1")).thenReturn(Optional.of(alarm));

        AlarmDTO result = alarmService.getAlarmById("1");

        assertNotNull(result);
        assertEquals(alarm.getId(), result.getId());
        assertEquals(alarm.getTitle(), result.getTitle());
    }

    @Test
    void getAlarmById_NotFound() {
        when(alarmRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            alarmService.getAlarmById("1");
        });
    }

    @Test
    void getAllAlarms() {
        List<Alarm> alarms = Arrays.asList(alarm);
        when(alarmRepository.findAll()).thenReturn(alarms);

        List<AlarmDTO> result = alarmService.getAllAlarms();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(alarm.getId(), result.get(0).getId());
    }

    @Test
    void getAlarmsByOrganizationUnit() {
        List<Alarm> alarms = Arrays.asList(alarm);
        when(alarmRepository.findByOrganizationUnitId("org1")).thenReturn(alarms);

        List<AlarmDTO> result = alarmService.getAlarmsByOrganizationUnit("org1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(alarm.getId(), result.get(0).getId());
    }

    @Test
    void getAlarmsByCalendar() {
        List<Alarm> alarms = Arrays.asList(alarm);
        when(alarmRepository.findByCalendarId("cal1")).thenReturn(alarms);

        List<AlarmDTO> result = alarmService.getAlarmsByCalendar("cal1");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(alarm.getId(), result.get(0).getId());
    }

    @Test
    void updateAlarm() {
        when(alarmRepository.findById("1")).thenReturn(Optional.of(alarm));
        when(alarmRepository.save(any(Alarm.class))).thenReturn(alarm);

        AlarmDTO result = alarmService.updateAlarm("1", alarmDTO);

        assertNotNull(result);
        assertEquals(alarmDTO.getId(), result.getId());
        assertEquals(alarmDTO.getTitle(), result.getTitle());
    }

    @Test
    void updateAlarm_NotFound() {
        when(alarmRepository.findById("1")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            alarmService.updateAlarm("1", alarmDTO);
        });
    }

    @Test
    void deleteAlarm() {
        doNothing().when(alarmRepository).deleteById("1");

        assertDoesNotThrow(() -> {
            alarmService.deleteAlarm("1");
        });

        verify(alarmRepository, times(1)).deleteById("1");
    }
}
