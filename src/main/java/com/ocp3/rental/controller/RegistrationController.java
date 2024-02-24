package com.ocp3.rental.controller;


import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBocp3Repository;

@RestController // Indique que cette classe est un contrôleur REST
public class RegistrationController {

    private final DBocp3Repository userRepository; // Référence vers le repository des utilisateurs
    private final PasswordEncoder rentalPasswordEncoder; // Référence vers l'encodeur de mots de passe

    // Constructeur avec injection de dépendances
    public RegistrationController(DBocp3Repository dbocp3Repository, PasswordEncoder passwordEncoder) {
        this.userRepository = dbocp3Repository;
        this.rentalPasswordEncoder = passwordEncoder;
    }

    @PostMapping("/api/auth/register") // Mappe cette méthode à l'URL "/api/auth/register" pour les requêtes POST
    public ResponseEntity<?> register(@RequestBody USERS users) { // Prend en paramètre un objet USERS qui est automatiquement converti à partir du corps de la requête HTTP
        // Vérifie si un utilisateur avec le même email existe déjà
        if (users.getEmail() == null || users.getEmail().isEmpty() || 
            users.getPassword() == null || users.getPassword().isEmpty() || 
            users.getName() == null || users.getName().isEmpty()) {
            // Si c'est le cas, renvoie une réponse avec le statut HTTP 400 (Bad Request) et un corps vide
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Encode le mot de passe de l'utilisateur
        users.setPassword(rentalPasswordEncoder.encode(users.getPassword()));
        // Crée un format de date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        // Obtient la date actuelle
        String currentDate = sdf.format(new Date());
        // Définit la date de création et de mise à jour de l'utilisateur à la date actuelle
        users.setCreated_at(currentDate);
        users.setUpdated_at(currentDate);
        // Enregistre l'utilisateur dans la base de données
        USERS savedUser = userRepository.save(users);

        // Renvoie une réponse avec l'utilisateur enregistré et le statut HTTP 201 (Created)
        return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
    }
}
