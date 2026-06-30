package de.thkoeln.ccq.firemanager.equipment.web;

import de.thkoeln.ccq.firemanager.equipment.model.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {

    public Equipment toEntity(EquipmentDto equipmentDto) {
        if (equipmentDto == null) {
            return null;
        }
        
        Equipment equipment = new Equipment();
        equipment.setId(equipmentDto.getId());
        equipment.setName(equipmentDto.getName());
        equipment.setSerialNumber(equipmentDto.getSerialNumber());
        
        return equipment;
    }

    public EquipmentDto toDto(Equipment equipment) {
        if (equipment == null) {
            return null;
        }
        
        EquipmentDto equipmentDto = new EquipmentDto();
        equipmentDto.setId(equipment.getId());
        equipmentDto.setName(equipment.getName());
        equipmentDto.setSerialNumber(equipment.getSerialNumber());
        
        return equipmentDto;
    }
}