package de.thkoeln.ccq.firemanager.inventory.mapper;

import de.thkoeln.ccq.firemanager.inventory.domain.Equipment;
import de.thkoeln.ccq.firemanager.inventory.dto.EquipmentCreateDTO;
import de.thkoeln.ccq.firemanager.inventory.dto.EquipmentDTO;
import de.thkoeln.ccq.firemanager.inventory.dto.EquipmentUpdateDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    
    Equipment toEntity(EquipmentCreateDTO dto);
    
    EquipmentDTO toDTO(Equipment entity);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(EquipmentUpdateDTO dto, @MappingTarget Equipment entity);
}