package lap_english.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import java.util.List;

@Configuration
@Profile({"dev", "prod"})
public class OpenApiConfig {
    @Bean
    public OpenAPI openAPI(@Value("${open.api.title}") String title,
                           @Value("${open.api.description}") String description,
                           @Value("${open.api.version}") String version,
                           @Value("${open.api.serverUrl}") String serverUrl,
                           @Value("${open.api.serverName}") String serverName) {
        return new OpenAPI().info(new Info().title(title)
                .description(description)
                .version(version)
                .license(new License().name("API License").url("http://domain.vn/lisicene"))).servers(List.of(new Server().url(serverUrl).description(serverName)))
        // them authentication bearer
                .components(new Components().addSecuritySchemes("bearer-auth", new SecurityScheme().type(SecurityScheme.Type.HTTP)
                        .scheme("bearer").bearerFormat("JWT"))).security(List.of(new SecurityRequirement().addList("bearer-auth")));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder().group("api-service-1").packagesToScan("lap_english.controller").build();
    }
}