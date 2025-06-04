package org.example.m295nick;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
        info = @Info(
                title = "Autovermietung API",
                version = "v1",
                description = "API für das Autovermietprojekt"
        )
)
@SpringBootApplication
public class M295NickApplication {

    public static void main(String[] args) {
        SpringApplication.run(M295NickApplication.class, args);
    }
}
