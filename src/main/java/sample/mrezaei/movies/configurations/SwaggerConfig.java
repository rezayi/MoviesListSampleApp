package sample.mrezaei.movies.configurations;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * This class customizes the OpenAPI and adds the apiKey authentication for Rest APIs
 */
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Movie API")
                        .version("1.0")
                        .description("API Documentation with API Key Authentication"))
                .addSecurityItem(new SecurityRequirement().addList("api_key"))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes("api_key", new SecurityScheme()
                                .name("api_key")   // The parameter name
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.QUERY)
                        ));
    }
}