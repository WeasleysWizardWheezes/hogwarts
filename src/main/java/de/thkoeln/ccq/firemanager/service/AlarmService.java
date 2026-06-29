package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.AlarmDTO;
import de.thkoeln.ccq.firemanager.model.Alarm;
import de.thkoeln.ccq.firemanager.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public AlarmDTO createAlarm(AlarmDTO alarmDTO) {
        Alarm alarm = mapToEntity(alarmDTO);
        Alarm savedAlarm = alarmRepository.save(alarm);
        return mapToDTO(savedAlarm);
    }

    public AlarmDTO getAlarmById(String id) {
        Alarm alarm = alarmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alarm not found"));
        return mapToDTO(alarm);
    }

    public List<AlarmDTO> getAllAlarms() {
        return alarmRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AlarmDTO> getAlarmsByOrganizationUnit(String organizationUnitId) {
        return alarmRepository.findByOrganizationUnitId(organizationUnitId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AlarmDTO> getAlarmsByCalendar(String calendarId) {
        return alarmRepository.findByCalendarId(calendarId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AlarmDTO updateAlarm(String id, AlarmDTO alarmDTO) {
        Alarm existingAlarm = alarmRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alarm not found"));
        
        existingAlarm.setTitle(alarmDTO.getTitle());
        existingAlarm.setDescription(alarmDTO.getDescription());
        existingAlarm.setStartDate(alarmDTO.getStartDate());
        existingAlarm.setEndDate(alarmDTO.getEndDate());
        existingAlarm.setRecurring(alarmDTO.isRecurring());
        existingAlarm.setRecurrencePattern(alarmDTO.getRecurrencePattern());
        
        Alarm updatedAlarm = alarmRepository.save(existingAlarm);
        return mapToDTO(updatedAlarm);
    }

    public void deleteAlarm(String id) {
        alarmRepository.deleteById(id);
    }

    private Alarm mapToEntity(AlarmDTO alarmDTO) {
        return Alarm.builder()
                .id(alarmDTO.getId())
                .title(alarmDTO.getTitle())
                .description(alarmDTO.getDescription())
                .startDate(alarmDTO.getStartDate())
                .endDate(alarmDTO.getEndDate())
                .isRecurring(alarmDTO.isRecurring())
                .recurrencePattern(alarmDTO.getRecurrencePattern())
                .build();
    }

    private AlarmDTO mapToDTO(Alarm alarm) {
        return AlarmDTO.builder()
                .id(alarm.getId())
                .title(alarm.getTitle())
                .description(alarm.getDescription())
                .startDate(alarm.getStartDate())
                .endDate(alarm.getEndDate())
                .isRecurring(alarm.isRecurring())
                .recurrencePattern(alarm.getRecurrencePattern())
                .organizationUnitId(alarm.getOrganizationUnit() != null ? alarm.getOrganizationUnit().getId() : null)
                .calendarId(alarm.getCalendar() != null ? alarm.getCalendar().getId() : null)
                .build();
    }
}
