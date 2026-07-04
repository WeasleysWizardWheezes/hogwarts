package de.thkoeln.ccq.firemanager.calendar.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface SpringDataCalendarRepository extends JpaRepository<JpaCalendarEntity, UUID> {
}