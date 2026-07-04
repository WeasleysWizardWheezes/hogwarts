package de.thkoeln.ccq.firemanager.calendar.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import java.util.List;

@Repository
public interface SpringDataEventRepository extends JpaRepository<JpaEventEntity, UUID> {
    @Query("SELECT e FROM JpaEventEntity e WHERE e.calendarId = :calendarId")
    List<JpaEventEntity> findByCalendarId(@Param("calendarId") UUID calendarId);
}