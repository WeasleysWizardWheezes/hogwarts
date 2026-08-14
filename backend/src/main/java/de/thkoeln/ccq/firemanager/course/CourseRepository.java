package de.thkoeln.ccq.firemanager.course;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface CourseRepository extends JpaRepository<Course, UUID> {
    
    List<Course> findByCategory(Course.CourseCategory category);
    
    @Query("SELECT c FROM Course c WHERE c.id IN :prerequisiteIds")
    List<Course> findPrerequisites(@Param("prerequisiteIds") List<UUID> prerequisiteIds);
    
    boolean existsByName(String name);
}
