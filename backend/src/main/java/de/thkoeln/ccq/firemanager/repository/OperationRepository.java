package de.thkoeln.ccq.firemanager.repository;

import de.thkoeln.ccq.firemanager.model.Operation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OperationRepository extends JpaRepository<Operation, UUID> {
}