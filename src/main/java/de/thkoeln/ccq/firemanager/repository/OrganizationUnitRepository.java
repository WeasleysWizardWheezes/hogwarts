package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.OrganizationUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrganizationUnitRepository extends JpaRepository<OrganizationUnit, String> {
    List<OrganizationUnit> findByNameContaining(String name);
}
