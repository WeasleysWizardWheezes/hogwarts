package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.AlarmDTO;
import de.thkoeln.ccq.firemanager.service.AlarmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlarmControllerTest {

    @Mock
    private AlarmService alarmService;

    @InjectMocks
    private AlarmController alarmController;

    private AlarmDTO alarmDTO;

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
    }

    @Test
    void createAlarm() {
        when(alarmService.createAlarm(any(AlarmDTO.class))).thenReturn(alarmDTO);

        ResponseEntity<AlarmDTO> response = alarmController.createAlarm(alarmDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(alarmDTO, response.getBody());
    }

    @Test
    void getAlarmById() {
        when(alarmService.getAlarmById(anyString())).thenReturn(alarmDTO);

        ResponseEntity<AlarmDTO> response = alarmController.getAlarmById("1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(alarmDTO, response.getBody());
    }

    @Test
    void getAllAlarms() {
        List<AlarmDTO> alarms = Arrays.asList(alarmDTO);
        when(alarmService.getAllAlarms()).thenReturn(alarms);

        ResponseEntity<List<AlarmDTO>> response = alarmController.getAllAlarms();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAlarmsByOrganizationUnit() {
        List<AlarmDTO> alarms = Arrays.asList(alarmDTO);
        when(alarmService.getAlarmsByOrganizationUnit(anyString())).thenReturn(alarms);

        ResponseEntity<List<AlarmDTO>> response = alarmController.getAlarmsByOrganizationUnit("org1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void getAlarmsByCalendar() {
        List<AlarmDTO> alarms = Arrays.asList(alarmDTO);
        when(alarmService.getAlarmsByCalendar(anyString())).thenReturn(alarms);

        ResponseEntity<List<AlarmDTO>> response = alarmController.getAlarmsByCalendar("cal1");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateAlarm() {
        when(alarmService.updateAlarm(anyString(), any(AlarmDTO.class))).thenReturn(alarmDTO);

        ResponseEntity<AlarmDTO> response = alarmController.updateAlarm("1", alarmDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(alarmDTO, response.getBody());
    }

    @Test
    void deleteAlarm() {
        doNothing().when(alarmService).deleteAlarm(anyString());

        ResponseEntity<Void> response = alarmController.deleteAlarm("1");

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
