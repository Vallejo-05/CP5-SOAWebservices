package br.com.fiap3esr.autoescola3esr.config.documentation;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Documentação automática (OpenAPI 3) gerada pelo springdoc.
// Swagger UI: http://localhost:8081/swagger-ui.html | JSON: /v3/api-docs | YAML: /v3/api-docs.yaml
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes(
                                "bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Informe o token JWT obtido em POST /login")
                        )
                )
                .info(new Info()
                        .title("Auto Escola 3ESR")
                        .version("v1")
                        .description("API REST para gerenciamento de instrutores, alunos, usuários e " +
                                "agendamento/cancelamento de instruções da auto escola 3ESR.")
                        .contact(new Contact()
                                .name("Auto Escola 3ESR")
                                .email("autoescola3esr@email.com.br")
                        )
                        .license(new License()
                                .name("Apache 2.0")
                                .url("http://springdoc.org")
                        )
                );
    }
}
