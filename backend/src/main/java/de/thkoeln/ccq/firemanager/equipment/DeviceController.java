package de.thkoeln.ccq.firemanager.equipment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/equipment/devices")
@Tag(name = "Equipment", description = "Operations for managing fire department equipment and devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @GetMapping
    @Operation(summary = "Get all devices", description = "Returns a list of all equipment devices")
    @ApiResponse(responseCode = "200", description = "Successful operation")
    public ResponseEntity<List<DeviceDto>> getAllDevices() {
        List<DeviceDto> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get device by ID", description = "Returns a single device by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
    })
    public ResponseEntity<DeviceDto> getDeviceById(
            @Parameter(description = "ID of the device to return", required = true)
            @PathVariable UUID id) {
        DeviceDto device = deviceService.getDeviceById(id);
        return ResponseEntity.ok(device);
    }

    @GetMapping("/serial-number/{serialNumber}")
    @Operation(summary = "Get device by serial number", description = "Returns a single device by its serial number")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful operation"),
            @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
    })
    public ResponseEntity<DeviceDto> getDeviceBySerialNumber(
            @Parameter(description = "Serial number of the device to return", required = true)
            @PathVariable String serialNumber) {
        DeviceDto device = deviceService.getDeviceBySerialNumber(serialNumber);
        return ResponseEntity.ok(device);
    }

    @PostMapping
    @Operation(summary = "Create a new device", description = "Creates a new equipment device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    public ResponseEntity<DeviceDto> createDevice(
            @Parameter(description = "Device to create", required = true)
            @Valid @RequestBody DeviceDto deviceDto) {
        DeviceDto createdDevice = deviceService.createDevice(deviceDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update device", description = "Updates an existing device (full update)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
    })
    public ResponseEntity<DeviceDto> updateDevice(
            @Parameter(description = "ID of the device to update", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Updated device data", required = true)
            @Valid @RequestBody DeviceDto deviceDto) {
        DeviceDto updatedDevice = deviceService.updateDevice(id, deviceDto);
        return ResponseEntity.ok(updatedDevice);
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Partially update device", description = "Partially updates an existing device")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
            @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
    })
    public ResponseEntity<DeviceDto> partialUpdateDevice(
            @Parameter(description = "ID of the device to update", required = true)
            @PathVariable UUID id,
            @Parameter(description = "Partial device data to update", required = true)
            @RequestBody DeviceDto deviceDto) {
        DeviceDto updatedDevice = deviceService.partialUpdateDevice(id, deviceDto);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete device", description = "Deletes a device (soft delete)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Device deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found", content = @Content)
    })
    public ResponseEntity<Void> deleteDevice(
            @Parameter(description = "ID of the device to delete", required = true)
            @PathVariable UUID id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}