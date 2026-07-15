package de.thkoeln.ccq.firemanager.equipment;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceMapper deviceMapper;

    public List<DeviceDto> getAllDevices() {
        return deviceRepository.findAllByArchivedFalse()
                .stream()
                .map(deviceMapper::toDto)
                .toList();
    }

    public DeviceDto getDeviceById(UUID id) {
        return deviceRepository.findActiveById(id)
                .map(deviceMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Device with id " + id + " not found"));
    }

    public DeviceDto getDeviceBySerialNumber(String serialNumber) {
        return deviceRepository.findActiveBySerialNumber(serialNumber)
                .map(deviceMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Device with serial number " + serialNumber + " not found"));
    }

    @Transactional
    public DeviceDto createDevice(DeviceDto deviceDto) {
        // Check if serial number already exists
        if (deviceRepository.findBySerialNumber(deviceDto.getSerialNumber()).isPresent()) {
            throw new IllegalArgumentException("Serial number " + deviceDto.getSerialNumber() + " already exists");
        }

        Device device = deviceMapper.toEntity(deviceDto);
        device.setArchived(false);
        Device savedDevice = deviceRepository.save(device);
        return deviceMapper.toDto(savedDevice);
    }

    @Transactional
    public DeviceDto updateDevice(UUID id, DeviceDto deviceDto) {
        Device existingDevice = deviceRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device with id " + id + " not found"));

        // Check if serial number is being changed to an existing one
        if (!existingDevice.getSerialNumber().equals(deviceDto.getSerialNumber()) &&
                deviceRepository.findBySerialNumber(deviceDto.getSerialNumber()).isPresent()) {
            throw new IllegalArgumentException("Serial number " + deviceDto.getSerialNumber() + " already exists");
        }

        Device updatedDevice = deviceMapper.updateEntityFromDto(deviceDto, existingDevice);
        Device savedDevice = deviceRepository.save(updatedDevice);
        return deviceMapper.toDto(savedDevice);
    }

    @Transactional
    public DeviceDto partialUpdateDevice(UUID id, DeviceDto deviceDto) {
        Device existingDevice = deviceRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device with id " + id + " not found"));

        // Check if serial number is being changed to an existing one
        if (deviceDto.getSerialNumber() != null &&
                !existingDevice.getSerialNumber().equals(deviceDto.getSerialNumber()) &&
                deviceRepository.findBySerialNumber(deviceDto.getSerialNumber()).isPresent()) {
            throw new IllegalArgumentException("Serial number " + deviceDto.getSerialNumber() + " already exists");
        }

        Device updatedDevice = deviceMapper.updateEntityFromDto(deviceDto, existingDevice);
        Device savedDevice = deviceRepository.save(updatedDevice);
        return deviceMapper.toDto(savedDevice);
    }

    @Transactional
    public void deleteDevice(UUID id) {
        Device device = deviceRepository.findActiveById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device with id " + id + " not found"));

        device.setArchived(true);
        device.setArchivedAt(java.time.Instant.now());
        deviceRepository.save(device);
    }
}