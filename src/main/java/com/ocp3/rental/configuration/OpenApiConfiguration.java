// Définition du package
package com.ocp3.rental.configuration;

// Importation des dépendances nécessaires
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Annotation pour indiquer que cette classe est une classe de configuration
@Configuration
public class OpenApiConfiguration {

    // Définition d'un bean OpenAPI pour la configuration de Swagger
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // Configuration des informations de l'API
                .info(apiInfo())
                // Configuration des composants de sécurité
                .components(new Components().addSecuritySchemes("bearerToken", securityScheme()));
    }

    // Méthode pour créer les informations de l'API
    private Info apiInfo() {
        return new Info()
                .title("RENTAL API") // Titre de l'API
                .version("1.0") // Version de l'API
                .description("RENTAL API Documentation with Swagger"); // Description de l'API
    }

    // Méthode pour créer le schéma de sécurité
    private SecurityScheme securityScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP) // Type de schéma de sécurité
                .scheme("bearer") // Schéma utilisé
                .bearerFormat("JWT"); // Format du token
    }
}