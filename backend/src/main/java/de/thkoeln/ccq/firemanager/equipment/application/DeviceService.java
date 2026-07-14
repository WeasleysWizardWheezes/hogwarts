package de.thkoeln.ccq.firemanager.equipment.application;

import de.thkoeln.ccq.firemanager.equipment.api.DeviceDto;
import de.thkoeln.ccq.firemanager.equipment.domain.Device;
import de.thkoeln.ccq.firemanager.equipment.domain.DeviceRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> getAllDevices() {
        return DeviceMapper.toDtoList(deviceRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<DeviceDto> getDeviceById(UUID id) {
        return deviceRepository.findById(id)
                .map(DeviceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<DeviceDto> getDeviceBySerialNumber(String serialNumber) {
        return deviceRepository.findBySerialNumber(serialNumber)
                .map(DeviceMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> searchDevicesByName(String name) {
        return DeviceMapper.toDtoList(deviceRepository.findByNameContainingIgnoreCase(name));
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> searchDevicesBySerialNumber(String serialNumber) {
        return DeviceMapper.toDtoList(deviceRepository.findBySerialNumberContainingIgnoreCase(serialNumber));
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> searchDevicesByType(String type) {
        return DeviceMapper.toDtoList(deviceRepository.findByTypeContainingIgnoreCase(type));
    }

    @Transactional(readOnly = true)
    public List<DeviceDto> searchDevicesByLocation(String location) {
        return DeviceMapper.toDtoList(deviceRepository.findByLocationContainingIgnoreCase(location));
    }

    public DeviceDto createDevice(DeviceDto deviceDto) {
        Device device = DeviceMapper.toEntity(deviceDto);
        Device savedDevice = deviceRepository.save(device);
        return DeviceMapper.toDto(savedDevice);
    }

    public DeviceDto updateDevice(UUID id, DeviceDto deviceDto) {
        return deviceRepository.findById(id)
                .map(existingDevice -> {
                    existingDevice.setName(deviceDto.name());
                    existingDevice.setSerialNumber(deviceDto.serialNumber());
                    existingDevice.setType(deviceDto.type());
                    existingDevice.setLocation(deviceDto.location());
                    Device updatedDevice = deviceRepository.save(existingDevice);
                    return DeviceMapper.toDto(updatedDevice);
                })
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    public void deleteDevice(UUID id) {
        deviceRepository.findById(id)
                .ifPresent(deviceRepository::delete);
    }

    public static class DeviceNotFoundException extends RuntimeException {
        public DeviceNotFoundException(UUID id) {
            super("Device not found with id: " + id);
        }
    }
}