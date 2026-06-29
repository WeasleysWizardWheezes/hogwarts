package de.thkoeln.ccq.firemanager.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Firemanager API")
                        .version("1.0")
                        .description("API für Dienst-, Termin- und Einsatzplanung")
                        .termsOfService("https://github.com/WeasleysWizardWheezes/hogwarts")
                        .license(new License()
                                .name("MIT License")
                                .url("https://github.com/WeasleysWizardWheezes/hogwarts/blob/main/LICENSE")));
    }
}
