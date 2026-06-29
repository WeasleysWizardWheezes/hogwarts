package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.CalendarDTO;
import de.thkoeln.ccq.firemanager.model.Calendar;
import de.thkoeln.ccq.firemanager.repository.CalendarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final CalendarRepository calendarRepository;

    public CalendarDTO createCalendar(CalendarDTO calendarDTO) {
        Calendar calendar = mapToEntity(calendarDTO);
        Calendar savedCalendar = calendarRepository.save(calendar);
        return mapToDTO(savedCalendar);
    }

    public CalendarDTO getCalendarById(String id) {
        Calendar calendar = calendarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calendar not found"));
        return mapToDTO(calendar);
    }

    public List<CalendarDTO> getAllCalendars() {
        return calendarRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<CalendarDTO> getCalendarsByOrganizationUnit(String organizationUnitId) {
        return calendarRepository.findByOrganizationUnitId(organizationUnitId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public CalendarDTO updateCalendar(String id, CalendarDTO calendarDTO) {
        Calendar existingCalendar = calendarRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Calendar not found"));
        
        existingCalendar.setName(calendarDTO.getName());
        existingCalendar.setDescription(calendarDTO.getDescription());
        existingCalendar.setPublic(calendarDTO.isPublic());
        
        Calendar updatedCalendar = calendarRepository.save(existingCalendar);
        return mapToDTO(updatedCalendar);
    }

    public void deleteCalendar(String id) {
        calendarRepository.deleteById(id);
    }

    private Calendar mapToEntity(CalendarDTO calendarDTO) {
        return Calendar.builder()
                .id(calendarDTO.getId())
                .name(calendarDTO.getName())
                .description(calendarDTO.getDescription())
                .isPublic(calendarDTO.isPublic())
                .build();
    }

    private CalendarDTO mapToDTO(Calendar calendar) {
        return CalendarDTO.builder()
                .id(calendar.getId())
                .name(calendar.getName())
                .description(calendar.getDescription())
                .isPublic(calendar.isPublic())
                .organizationUnitId(calendar.getOrganizationUnit() != null ? calendar.getOrganizationUnit().getId() : null)
                .build();
    }
}
