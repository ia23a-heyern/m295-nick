package org.example.m295nick.configs;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Konfiguriert Swagger/OpenAPI und legt das HTTP-Basic-SecurityScheme fest.
 */
@Configuration
@SecurityScheme(
        name = "basicAuth",                // Interner Name des Schemas, referenziert von @SecurityRequirement
        type = SecuritySchemeType.HTTP,    // HTTP-Schema
        scheme = "basic"                   // Basic Auth
)
public class OpenAPIConfig {

    /**
     * Optional: Allgemeine OpenAPI-Infos (Titel, Version, Kontakt etc.).
     * Du kannst diesen Bean-Teil weglassen, wenn du bereits eine andere OpenAPI-Config hast.
     */
    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Auto­vermietung API")
                        .description("REST-Schnittstelle für Vehicle und Rental mit HTTP-Basic Auth")
                        .version("v1.0")
                        .contact(new Contact()
                                .name("Nick Heyer")
                                .email("heyern@bzz.ch")
                        )
                )
                .externalDocs(new ExternalDocumentation()
                        .description("Projekt-Repository")
                        .url("https://github.com/nick‐heyer/autovermietung")
                );
    }
}
