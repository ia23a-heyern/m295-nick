package org.example.m295nick;

import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI apiInfo() {
        return new OpenAPI()
                .info(new Info()
                        .title("Vehicle Rental API")
                        .description("REST-API for vehicle rental management")
                        .version("1.0")
                        .contact(new Contact()
                                .name("Nick")
                                .email("heyern@bzz.ch")));
    }
}
