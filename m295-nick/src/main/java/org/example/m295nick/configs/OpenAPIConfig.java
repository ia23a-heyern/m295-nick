package org.example.m295nick.configs;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        // 1. Definiere einen SecurityScheme mit dem Typ HTTP, scheme = "basic"
        SecurityScheme basicAuthScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("basic");

        // 2. Füge den SecurityScheme zu den Components hinzu unter dem Key "basicAuth"
        Components components = new Components()
                .addSecuritySchemes("basicAuth", basicAuthScheme);

        // 3. Erzeuge eine SecurityRequirement, die das Scheme "basicAuth" verwendet
        SecurityRequirement securityRequirement = new SecurityRequirement()
                .addList("basicAuth"); // der Name hier muss mit dem Key aus addSecuritySchemes übereinstimmen

        // 4. Baue die OpenAPI-Definition zusammen
        return new OpenAPI()
                .components(components)
                .addSecurityItem(securityRequirement)
                .info(new Info()
                        .title("Vehicle Rental API")
                        .description("REST-API for vehicle rental management")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Nick")
                                .email("heyern@bzz.ch")
                        )
                );
    }
}
