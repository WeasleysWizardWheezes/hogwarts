package de.thkoeln.ccq.firemanager.equipment.api;

import de.thkoeln.ccq.firemanager.equipment.application.DeviceService;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/v1/equipment/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public List<DeviceDto> getAllDevices(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String serialNumber,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String location) {
        if (name != null) {
            return deviceService.searchDevicesByName(name);
        }
        if (serialNumber != null) {
            return deviceService.searchDevicesBySerialNumber(serialNumber);
        }
        if (type != null) {
            return deviceService.searchDevicesByType(type);
        }
        if (location != null) {
            return deviceService.searchDevicesByLocation(location);
        }
        return deviceService.getAllDevices();
    }

    @GetMapping("/{id}")
    public DeviceDto getDeviceById(@PathVariable UUID id) {
        return deviceService.getDeviceById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    @GetMapping("/serial-number/{serialNumber}")
    public DeviceDto getDeviceBySerialNumber(@PathVariable String serialNumber) {
        return deviceService.getDeviceBySerialNumber(serialNumber)
                .orElseThrow(() -> new DeviceNotFoundException(null));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DeviceDto createDevice(@RequestBody DeviceDto deviceDto) {
        return deviceService.createDevice(deviceDto);
    }

    @PutMapping("/{id}")
    public DeviceDto updateDevice(@PathVariable UUID id, @RequestBody DeviceDto deviceDto) {
        return deviceService.updateDevice(id, deviceDto);
    }

    @PatchMapping("/{id}")
    public DeviceDto partialUpdateDevice(@PathVariable UUID id, @RequestBody DeviceDto deviceDto) {
        return deviceService.updateDevice(id, deviceDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDevice(@PathVariable UUID id) {
        deviceService.deleteDevice(id);
    }

    public static class DeviceNotFoundException extends RuntimeException {
        public DeviceNotFoundException(UUID id) {
            super("Device not found with id: " + id);
        }
    }
}