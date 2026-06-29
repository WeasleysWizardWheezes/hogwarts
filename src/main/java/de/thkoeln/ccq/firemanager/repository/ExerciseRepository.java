package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, String> {
    List<Exercise> findByOrganizationUnitId(String organizationUnitId);
    List<Exercise> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
    List<Exercise> findByIsRecurring(boolean isRecurring);
}
