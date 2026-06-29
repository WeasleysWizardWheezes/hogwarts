package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.Calendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository<Calendar, String> {
    List<Calendar> findByNameContaining(String name);
    List<Calendar> findByOrganizationUnitId(String organizationUnitId);
    List<Calendar> findByIsPublic(boolean isPublic);
}
