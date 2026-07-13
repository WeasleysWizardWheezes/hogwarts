package de.thkoeln.ccq.firemanager;

import io.swagger.v3.oas.models.media.Schema;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springdoc.core.customizers.OpenApiCustomizer;
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
