package de.thkoeln.ccq.firemanager.equipment;

import org.springframework.stereotype.Component;

@Component
public class DeviceMapper {

    public DeviceDto toDto(Device device) {
        return DeviceDto.fromEntity(device);
    }

    public Device toEntity(DeviceDto deviceDto) {
        return deviceDto.toEntity();
    }

    public Device updateEntityFromDto(DeviceDto deviceDto, Device existingDevice) {
        if (deviceDto.getName() != null) {
            existingDevice.setName(deviceDto.getName());
        }
        if (deviceDto.getSerialNumber() != null) {
            existingDevice.setSerialNumber(deviceDto.getSerialNumber());
        }
        if (deviceDto.getType() != null) {
            existingDevice.setType(deviceDto.getType());
        }
        if (deviceDto.getLocation() != null) {
            existingDevice.setLocation(deviceDto.getLocation());
        }
        return existingDevice;
    }
}