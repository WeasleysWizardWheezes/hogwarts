package de.thkoeln.ccq.firemanager.equipment.application;

import de.thkoeln.ccq.firemanager.equipment.api.DeviceDto;
import de.thkoeln.ccq.firemanager.equipment.domain.Device;
import java.util.List;
import java.util.stream.Collectors;

public class DeviceMapper {

    public static DeviceDto toDto(Device device) {
        if (device == null) {
            return null;
        }
        return new DeviceDto(
                device.getId(),
                device.getName(),
                device.getSerialNumber(),
                device.getType(),
                device.getLocation()
        );
    }

    public static Device toEntity(DeviceDto dto) {
        if (dto == null) {
            return null;
        }
        Device device = new Device();
        device.setId(dto.id());
        device.setName(dto.name());
        device.setSerialNumber(dto.serialNumber());
        device.setType(dto.type());
        device.setLocation(dto.location());
        return device;
    }

    public static List<DeviceDto> toDtoList(List<Device> devices) {
        return devices.stream()
                .map(DeviceMapper::toDto)
                .collect(Collectors.toList());
    }
}