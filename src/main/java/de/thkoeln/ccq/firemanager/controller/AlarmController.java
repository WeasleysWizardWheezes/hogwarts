package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.AlarmDTO;
import de.thkoeln.ccq.firemanager.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alarms")
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @PostMapping
    public ResponseEntity<AlarmDTO> createAlarm(@RequestBody AlarmDTO alarmDTO) {
        AlarmDTO createdAlarm = alarmService.createAlarm(alarmDTO);
        return new ResponseEntity<>(createdAlarm, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlarmDTO> getAlarmById(@PathVariable String id) {
        AlarmDTO alarmDTO = alarmService.getAlarmById(id);
        return ResponseEntity.ok(alarmDTO);
    }

    @GetMapping
    public ResponseEntity<List<AlarmDTO>> getAllAlarms() {
        List<AlarmDTO> alarms = alarmService.getAllAlarms();
        return ResponseEntity.ok(alarms);
    }

    @GetMapping("/organization-unit/{organizationUnitId}")
    public ResponseEntity<List<AlarmDTO>> getAlarmsByOrganizationUnit(@PathVariable String organizationUnitId) {
        List<AlarmDTO> alarms = alarmService.getAlarmsByOrganizationUnit(organizationUnitId);
        return ResponseEntity.ok(alarms);
    }

    @GetMapping("/calendar/{calendarId}")
    public ResponseEntity<List<AlarmDTO>> getAlarmsByCalendar(@PathVariable String calendarId) {
        List<AlarmDTO> alarms = alarmService.getAlarmsByCalendar(calendarId);
        return ResponseEntity.ok(alarms);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlarmDTO> updateAlarm(@PathVariable String id, @RequestBody AlarmDTO alarmDTO) {
        AlarmDTO updatedAlarm = alarmService.updateAlarm(id, alarmDTO);
        return ResponseEntity.ok(updatedAlarm);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlarm(@PathVariable String id) {
        alarmService.deleteAlarm(id);
        return ResponseEntity.noContent().build();
    }
}
