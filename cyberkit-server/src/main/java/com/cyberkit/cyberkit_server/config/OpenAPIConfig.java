package com.cyberkit.cyberkit_server.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenAPIConfig {

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

    private Server createServer(String url, String description) {
        Server server = new Server();
        server.setUrl(url);
        server.setDescription(description);
        return server;
    }

    private Contact createContact() {
        return new Contact()
                .email("cyberkit@gmail.com")
                .name("Cyberkit")
                .url("https://cyberkit.vn");
    }

    private License createLicense() {
        return new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");
    }

    private Info createApiInfo() {
        return new Info()
                .title("Cyberkit WebApp API")
                .version("1.0")
                .contact(createContact())
                .description("This API exposes all endpoints.")
                .termsOfService("https://")
                .license(createLicense());
    }

    @Bean
    public OpenAPI myOpenAPI() {
        return new OpenAPI()
                .info(createApiInfo())
                .servers(List.of(
                        createServer("http://localhost:8080/cyberkit/", "Server URL in Development environment")))
                .addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()));
    }
}