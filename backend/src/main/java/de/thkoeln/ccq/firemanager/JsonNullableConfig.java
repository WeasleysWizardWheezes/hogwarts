package de.thkoeln.ccq.firemanager;

import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.BeanProperty;
import tools.jackson.databind.JavaType;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.JsonGenerator;

@Configuration
public class JsonNullableConfig {

    @Bean
    public SimpleModule jsonNullableModule() {
        SimpleModule module = new SimpleModule("JsonNullableModule");
        module.addDeserializer(JsonNullable.class, new JsonNullableDeserializer());
        module.addSerializer(JsonNullable.class, new JsonNullableSerializer());
        return module;
    }

    private static final class JsonNullableSerializer extends ValueSerializer<JsonNullable> {

        @Override
        public void serialize(
                JsonNullable value,
                JsonGenerator generator,
                tools.jackson.databind.SerializationContext context
        ) {
            if (value == null || !value.isPresent()) {
                generator.writeNull();
                return;
            }
            ValueSerializer<Object> serializer = context.findValueSerializer(value.get().getClass());
            serializer.serialize(value.get(), generator, context);
        }
    }

    private static final class JsonNullableDeserializer extends ValueDeserializer<JsonNullable<?>> {

        private final JavaType valueType;

        private JsonNullableDeserializer() {
            this.valueType = null;
        }

        private JsonNullableDeserializer(JavaType valueType) {
            this.valueType = valueType;
        }

        @Override
        public ValueDeserializer<?> createContextual(
                DeserializationContext context,
                BeanProperty property
        ) {
            JavaType propertyType = property.getType();
            return new JsonNullableDeserializer(propertyType.containedTypeOrUnknown(0));
        }

        @Override
        public JsonNullable<?> deserialize(
                JsonParser parser,
                DeserializationContext context
        ) {
            if (parser.currentToken() == JsonToken.VALUE_NULL) {
                return JsonNullable.of(null);
            }
            Object value = context.readValue(parser, valueType);
            return JsonNullable.of(value);
        }
    }
}