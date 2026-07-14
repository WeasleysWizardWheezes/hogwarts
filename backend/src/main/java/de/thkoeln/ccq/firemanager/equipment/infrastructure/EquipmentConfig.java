package de.thkoeln.ccq.firemanager.equipment.infrastructure;

import de.thkoeln.ccq.firemanager.equipment.domain.EquipmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EquipmentConfig {
    @Bean
    public EquipmentRepository equipmentRepository(
        SpringDataEquipmentRepository springDataEquipmentRepository,
        EquipmentPersistenceMapper mapper
    ) {
        return new JpaEquipmentRepository(springDataEquipmentRepository, mapper);
    }
}
