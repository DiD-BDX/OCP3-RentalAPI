package com.ocp3.rental.controller;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.DTO.UsersDataTransferObject;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBocp3Repository;
import com.ocp3.rental.service.CustomUserDetailsService;
import com.ocp3.rental.service.JWTService;

@RestController // Indique que cette classe est un contrôleur REST
public class RegistrationController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Référence vers le service de gestion des utilisateurs
    @Autowired
    private DBocp3Repository dbocp3repository; // Référence vers le repository des utilisateurs
    @Autowired // Injection de dépendances pour AuthenticationManager, UserDetailsService et JWTService
    private  UserDetailsService userDetailsService;
    @Autowired // Injection de dépendances pour AuthenticationManager, UserDetailsService et JWTService
    private  JWTService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/api/auth/register") // Mappe cette méthode à l'URL "/api/auth/register" pour les requêtes POST
    public ResponseEntity<?> register(@RequestBody UsersDataTransferObject usersDto) { // Prend en paramètre un objet usersDto qui est automatiquement converti à partir du corps de la requête HTTP
        System.out.println("Starting register method...");
        System.out.println(usersDto);
        
        // Vérifie si un utilisateur avec le même email existe déjà
        Optional<USERS> existingUser = dbocp3repository.findByEmail(usersDto.getEmail());
        if (existingUser.isPresent()) {
            // Si c'est le cas, renvoie une réponse avec le statut HTTP 400 (Bad Request) et un message d'erreur
            return new ResponseEntity<>("{\"error\":\"Existing user with this Email\"}", HttpStatus.BAD_REQUEST);
        }

        usersDto.setPassword(usersDto.getPassword());
        usersDto.setCreated_at(LocalDate.now());
        usersDto.setUpdated_at(LocalDate.now());
        System.out.println("UserDto avant enr dans la DB: " + usersDto);
        // encodage du password avant enregistrement dans la DB
        usersDto.setPassword(passwordEncoder.encode(usersDto.getPassword()));
        
        // Crée une nouvelle entité USERS et l'enregistre dans la base de données
        customUserDetailsService.registerUser(usersDto);

        // Charge les détails de l'utilisateur à partir de l'email fourni
        final UserDetails userDetails = userDetailsService.loadUserByUsername(usersDto.getEmail());
        // Crée un objet Authentication avec le nom d'utilisateur et aucun mot de passe
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null);
        // Génère un token JWT à partir de l'objet Authentication
        final String token = jwtService.generateToken(authentication);
        // Crée une map avec le token JWT
        Map<String, String> tokenMap = new HashMap<String, String>();
        tokenMap.put("token", token);
        // Renvoie une réponse avec la map du token JWT
        return ResponseEntity.ok(tokenMap);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>("{}", headers, HttpStatus.BAD_REQUEST);
    }
}
