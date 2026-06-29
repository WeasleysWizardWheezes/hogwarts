package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.Rule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RuleRepository extends JpaRepository<Rule, String> {
    List<Rule> findByNameContaining(String name);
}
