package com.globetrotter.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Globetrotter Services API")
                        .description("Travel information and AI capabilities through a comprehensive API")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Globetrotter Services API Support")
                                .url("https://api.globetrotter-services.com/support")
                                .email("api-support@globetrotter-services.com"))
                        .license(new License()
                                .name("Globetrotter Services License")
                                .url("https://api.globetrotter-services.com/license")))
                .servers(Arrays.asList(
                        new Server().url("https://api.globetrotter-services.com").description("Production"),
                        new Server().url("http://localhost:8080").description("Local Development")))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .name("BearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Enter your API key as the Bearer token")));
    }
}
