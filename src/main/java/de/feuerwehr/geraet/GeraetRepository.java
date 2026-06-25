package de.feuerwehr.geraet;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface GeraetRepository extends JpaRepository<Geraet, UUID> {
}