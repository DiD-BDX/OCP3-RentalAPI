// Définition du package
package com.ocp3.rental.DTO;

// Importation des dépendances nécessaires
import java.math.BigDecimal;
import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Utilisation de l'annotation @Data de Lombok pour générer automatiquement les getters, setters, equals, hashCode et toString
// Utilisation de @AllArgsConstructor pour générer un constructeur avec tous les arguments
// Utilisation de @NoArgsConstructor pour générer un constructeur sans argument
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalsDataTransferObject {
    // Définition des champs de l'objet
    private Integer id; // L'identifiant de la location
    private String name; // Le nom de la location
    private BigDecimal surface; // La surface de la location
    private BigDecimal price; // Le prix de la location
    private MultipartFile picture; // L'image de la location
    private String pictureUrl; // L'URL de l'image de la location
    private String description; // La description de la location
    private Integer ownerId; // L'identifiant du propriétaire de la location
    private LocalDate created_at; // La date de création de la location
    private LocalDate updated_at; // La date de mise à jour de la location
}