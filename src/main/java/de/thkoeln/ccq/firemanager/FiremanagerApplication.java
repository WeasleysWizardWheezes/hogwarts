package de.thkoeln.ccq.firemanager;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
    info = @Info(
        title = "Firemanager API",
        version = "1.0",
        description = "API für Dienst-, Termin- und Einsatzplanung"
    )
)
public class FiremanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(FiremanagerApplication.class, args);
	}

}
