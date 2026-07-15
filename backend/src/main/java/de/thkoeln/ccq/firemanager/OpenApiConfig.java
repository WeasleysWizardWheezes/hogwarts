package de.thkoeln.ccq.firemanager;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Global springdoc configuration.
 * Marks all non-nullable schema properties as required,
 * so the generated OpenAPI spec accurately reflects Java records and non-nullable fields.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("FireManager API")
                        .version("1.0.0")
                        .description("API for managing fire department resources and operations")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")));
    }

    @Bean
    public GroupedOpenApi equipmentApi() {
        return GroupedOpenApi.builder()
                .group("equipment")
                .pathsToMatch("/api/v1/equipment/**")
                .build();
    }

    @Bean
    public OpenApiCustomizer requireNonNullablePropertiesCustomizer() {
        return openApi -> {
            Map<String, Schema> schemas = openApi.getComponents().getSchemas();
            if (schemas == null) {
                return;
            }
            schemas.values().forEach(this::markNonNullableAsRequired);
        };
    }

    @SuppressWarnings("unchecked")
    private void markNonNullableAsRequired(Schema<?> schema) {
        Map<String, Schema> properties = schema.getProperties();
        if (properties == null) {
            return;
        }

        List<String> requiredFields = new ArrayList<>();
        properties.forEach((name, property) -> {
            if (!Boolean.TRUE.equals(property.getNullable())) {
                requiredFields.add(name);
            }
        });

        if (!requiredFields.isEmpty()) {
            schema.setRequired(requiredFields);
        }
    }
}
