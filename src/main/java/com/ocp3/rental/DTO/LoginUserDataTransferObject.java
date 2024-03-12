// Définition du package
package com.ocp3.rental.DTO;

// Importation des dépendances nécessaires
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Utilisation de l'annotation @Data de Lombok pour générer automatiquement les getters, setters, equals, hashCode et toString
// Utilisation de @AllArgsConstructor pour générer un constructeur avec tous les arguments
// Utilisation de @NoArgsConstructor pour générer un constructeur sans argument
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginUserDataTransferObject {
    // Définition des champs de l'objet avec l'annotation @Schema pour la documentation Swagger
    // Le champ est en lecture seule dans la documentation Swagger
    @Schema(readOnly = true)
    private Integer id;

    // Exemple de valeur pour la documentation Swagger
    @Schema(example = "test@test.com")
    private String email;

    // Le champ est en lecture seule dans la documentation Swagger
    @Schema(readOnly = true)
    private String name;

    // Exemple de valeur pour la documentation Swagger
    @Schema(example = "test!31")
    private String password;

    // Le champ est en lecture seule dans la documentation Swagger
    @Schema(readOnly = true)
    private LocalDate created_at;

    // Le champ est en lecture seule dans la documentation Swagger
    @Schema(readOnly = true)
    private LocalDate updated_at;
}