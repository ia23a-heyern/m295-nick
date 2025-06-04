package org.example.m295nick.configs;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI autovermietungOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Autovermietung API")
                        .version("v1.0")
                        .description("REST‐Schnittstelle für das Autovermietprojekt"))
                .externalDocs(new ExternalDocumentation()
                        .description("Projekt‐Dokumentation")
                        .url("https://github.com/deinNutzername/autovermietung"));
    }
}
