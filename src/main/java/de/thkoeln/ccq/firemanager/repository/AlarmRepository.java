package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, String> {
    List<Alarm> findByOrganizationUnitId(String organizationUnitId);
    List<Alarm> findByCalendarId(String calendarId);
    List<Alarm> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    List<Alarm> findByIsRecurring(boolean isRecurring);
}
