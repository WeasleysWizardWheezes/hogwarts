package de.thkoeln.ccq.firemanager.equipment;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DeviceDto {

    private UUID id;

    @NotBlank(message = "Gerätename ist erforderlich")
    private String name;

    @NotBlank(message = "Seriennummer ist erforderlich")
    private String serialNumber;

    @NotBlank(message = "Typ ist erforderlich")
    private String type;

    @NotBlank(message = "Lagerort ist erforderlich")
    private String location;

    public static DeviceDto fromEntity(Device device) {
        return DeviceDto.builder()
                .id(device.getId())
                .name(device.getName())
                .serialNumber(device.getSerialNumber())
                .type(device.getType())
                .location(device.getLocation())
                .build();
    }

    public Device toEntity() {
        return Device.builder()
                .id(this.id)
                .name(this.name)
                .serialNumber(this.serialNumber)
                .type(this.type)
                .location(this.location)
                .build();
    }
}