package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, String> {
    List<Appointment> findByOrganizationUnitId(String organizationUnitId);
    List<Appointment> findByCalendarId(String calendarId);
    List<Appointment> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    List<Appointment> findByIsRecurring(boolean isRecurring);
}
