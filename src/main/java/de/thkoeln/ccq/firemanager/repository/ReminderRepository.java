package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, String> {
    List<Reminder> findByAppointmentId(String appointmentId);
    List<Reminder> findByUserId(String userId);
    List<Reminder> findByIsRecurring(boolean isRecurring);
}
