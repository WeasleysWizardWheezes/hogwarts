package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.dto.AppointmentDTO;
import de.thkoeln.ccq.firemanager.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentDTO> createAppointment(@RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO createdAppointment = appointmentService.createAppointment(appointmentDTO);
        return new ResponseEntity<>(createdAppointment, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppointmentById(@PathVariable String id) {
        AppointmentDTO appointmentDTO = appointmentService.getAppointmentById(id);
        return ResponseEntity.ok(appointmentDTO);
    }

    @GetMapping
    public ResponseEntity<List<AppointmentDTO>> getAllAppointments() {
        List<AppointmentDTO> appointments = appointmentService.getAllAppointments();
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/organization-unit/{organizationUnitId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByOrganizationUnit(@PathVariable String organizationUnitId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByOrganizationUnit(organizationUnitId);
        return ResponseEntity.ok(appointments);
    }

    @GetMapping("/calendar/{calendarId}")
    public ResponseEntity<List<AppointmentDTO>> getAppointmentsByCalendar(@PathVariable String calendarId) {
        List<AppointmentDTO> appointments = appointmentService.getAppointmentsByCalendar(calendarId);
        return ResponseEntity.ok(appointments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateAppointment(@PathVariable String id, @RequestBody AppointmentDTO appointmentDTO) {
        AppointmentDTO updatedAppointment = appointmentService.updateAppointment(id, appointmentDTO);
        return ResponseEntity.ok(updatedAppointment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable String id) {
        appointmentService.deleteAppointment(id);
        return ResponseEntity.noContent().build();
    }
}
