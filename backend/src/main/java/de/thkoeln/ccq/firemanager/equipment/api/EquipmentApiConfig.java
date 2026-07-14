package de.thkoeln.ccq.firemanager.equipment.api;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EquipmentApiConfig {
    @Bean
    public GroupedOpenApi equipmentApi() {
        return GroupedOpenApi.builder()
            .group("equipment")
            .pathsToMatch("/api/material-geraete/**")
            .build();
    }
}
