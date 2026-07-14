package de.thkoeln.ccq.firemanager.equipment.api;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(
    info = @Info(
        title = "Material- und Geräteinventar API",
        description = "API für die Verwaltung von Material und Geräten der Feuerwehr",
        version = "1.0.0"
    ),
    servers = {
        @Server(url = "http://localhost:8080/api", description = "Local development server"),
        @Server(url = "https://api.hogwarts-feuerwehr.de/api", description = "Production server")
    }
)
public class EquipmentOpenApiConfig {
}
