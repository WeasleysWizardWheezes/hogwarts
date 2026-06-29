package de.thkoeln.ccq.firemanager.controller;

import de.thkoeln.ccq.firemanager.model.Device;
import de.thkoeln.ccq.firemanager.repository.DeviceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeviceControllerTest {

    @Mock
    private DeviceRepository deviceRepository;

    @InjectMocks
    private DeviceController deviceController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createDevice() {
        Device device = new Device("Feuerlöscher", 5.0, LocalDate.of(2025, 12, 31));
        when(deviceRepository.save(any(Device.class))).thenReturn(device);

        ResponseEntity<Device> response = deviceController.createDevice(device);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(device, response.getBody());
    }

    @Test
    void getDeviceById() {
        String deviceId = "123";
        Device device = new Device("Feuerlöscher", 5.0, LocalDate.of(2025, 12, 31));
        device.setId(deviceId);
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.of(device));

        ResponseEntity<Device> response = deviceController.getDeviceById(deviceId);

        assertEquals(200, response.getStatusCode().value());
        assertEquals(device, response.getBody());
    }

    @Test
    void getDeviceByIdNotFound() {
        String deviceId = "123";
        when(deviceRepository.findById(deviceId)).thenReturn(Optional.empty());

        ResponseEntity<Device> response = deviceController.getDeviceById(deviceId);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void getAllDevices() {
        Device device1 = new Device("Feuerlöscher", 5.0, LocalDate.of(2025, 12, 31));
        Device device2 = new Device("Atemschutzmaske", 2.5, LocalDate.of(2026, 6, 30));
        List<Device> devices = Arrays.asList(device1, device2);
        when(deviceRepository.findAll()).thenReturn(devices);

        ResponseEntity<List<Device>> response = deviceController.getAllDevices();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(devices, response.getBody());
    }
}
