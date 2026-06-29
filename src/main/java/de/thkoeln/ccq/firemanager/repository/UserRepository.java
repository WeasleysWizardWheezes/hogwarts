package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
}
