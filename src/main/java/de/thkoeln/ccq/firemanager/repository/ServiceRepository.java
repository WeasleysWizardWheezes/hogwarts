package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, String> {
    List<Service> findByOrganizationUnitId(String organizationUnitId);
    List<Service> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    List<Service> findByIsRecurring(boolean isRecurring);
}
