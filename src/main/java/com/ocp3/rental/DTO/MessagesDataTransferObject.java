// Définition du package
package com.ocp3.rental.DTO;

import java.time.LocalDate;

// Importation des dépendances nécessaires
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Utilisation de l'annotation @Data de Lombok pour générer automatiquement les getters, setters, equals, hashCode et toString
// Utilisation de @AllArgsConstructor pour générer un constructeur avec tous les arguments
// Utilisation de @NoArgsConstructor pour générer un constructeur sans argument
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagesDataTransferObject {
    // Définition des champs de l'objet
    private String message; // Le message
    private Integer user_id; // L'identifiant de l'utilisateur
    private Integer rental_id; // L'identifiant de la location
    private LocalDate created_at; // La date de création
    private LocalDate updated_at; // La date de mise à jour
}