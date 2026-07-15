package com.hogwarts.vehiclemanagement.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {

    private Long id;

    private String name;

    private String radioCallName;

    private String licensePlate;

    private Integer year;

    private String description;

    private Long vehicleGroupId;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private boolean deleted;
}
