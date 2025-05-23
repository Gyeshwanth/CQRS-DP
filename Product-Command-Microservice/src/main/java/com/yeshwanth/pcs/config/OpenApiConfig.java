package com.yeshwanth.pcs.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productCommandOpenAPI() {
        Server localServer = new Server();
        localServer.setUrl("http://localhost:8081");
        localServer.setDescription("Local Development Server");

        Server dockerServer = new Server();
        dockerServer.setUrl("http://product-command-service:8081");
        dockerServer.setDescription("Docker Development Server");

        Contact contact = new Contact();
        contact.setName("Yeshwanth");
        contact.setEmail("yeshwanth@example.com");

        License license = new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0");

        Info info = new Info()
                .title("Product Command Microservice API")
                .version("1.0")
                .description("This API provides endpoints to manage products in the command microservice")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(localServer, dockerServer));
    }
}