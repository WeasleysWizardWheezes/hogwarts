package de.thkoeln.ccq.firemanager.service;

import de.thkoeln.ccq.firemanager.dto.AppointmentDTO;
import de.thkoeln.ccq.firemanager.model.Appointment;
import de.thkoeln.ccq.firemanager.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    public AppointmentDTO createAppointment(AppointmentDTO appointmentDTO) {
        Appointment appointment = mapToEntity(appointmentDTO);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        return mapToDTO(savedAppointment);
    }

    public AppointmentDTO getAppointmentById(String id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        return mapToDTO(appointment);
    }

    public List<AppointmentDTO> getAllAppointments() {
        return appointmentRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByOrganizationUnit(String organizationUnitId) {
        return appointmentRepository.findByOrganizationUnitId(organizationUnitId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<AppointmentDTO> getAppointmentsByCalendar(String calendarId) {
        return appointmentRepository.findByCalendarId(calendarId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public AppointmentDTO updateAppointment(String id, AppointmentDTO appointmentDTO) {
        Appointment existingAppointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
        
        existingAppointment.setTitle(appointmentDTO.getTitle());
        existingAppointment.setDescription(appointmentDTO.getDescription());
        existingAppointment.setStartDate(appointmentDTO.getStartDate());
        existingAppointment.setEndDate(appointmentDTO.getEndDate());
        existingAppointment.setRecurring(appointmentDTO.isRecurring());
        existingAppointment.setRecurrencePattern(appointmentDTO.getRecurrencePattern());
        
        Appointment updatedAppointment = appointmentRepository.save(existingAppointment);
        return mapToDTO(updatedAppointment);
    }

    public void deleteAppointment(String id) {
        appointmentRepository.deleteById(id);
    }

    private Appointment mapToEntity(AppointmentDTO appointmentDTO) {
        return Appointment.builder()
                .id(appointmentDTO.getId())
                .title(appointmentDTO.getTitle())
                .description(appointmentDTO.getDescription())
                .startDate(appointmentDTO.getStartDate())
                .endDate(appointmentDTO.getEndDate())
                .isRecurring(appointmentDTO.isRecurring())
                .recurrencePattern(appointmentDTO.getRecurrencePattern())
                .build();
    }

    private AppointmentDTO mapToDTO(Appointment appointment) {
        return AppointmentDTO.builder()
                .id(appointment.getId())
                .title(appointment.getTitle())
                .description(appointment.getDescription())
                .startDate(appointment.getStartDate())
                .endDate(appointment.getEndDate())
                .isRecurring(appointment.isRecurring())
                .recurrencePattern(appointment.getRecurrencePattern())
                .organizationUnitId(appointment.getOrganizationUnit() != null ? appointment.getOrganizationUnit().getId() : null)
                .calendarId(appointment.getCalendar() != null ? appointment.getCalendar().getId() : null)
                .build();
    }
}
