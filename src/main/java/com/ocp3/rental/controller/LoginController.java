package com.ocp3.rental.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.ocp3.rental.model.USERS;
import com.ocp3.rental.service.JWTService;



@RestController // Indique que cette classe est un contrôleur REST
public class LoginController {
    @Autowired // Injection de dépendances pour AuthenticationManager, UserDetailsService et JWTService
    private  AuthenticationManager authManager;
    @Autowired // Injection de dépendances pour AuthenticationManager, UserDetailsService et JWTService
    private  UserDetailsService userDetailsService;
    @Autowired // Injection de dépendances pour AuthenticationManager, UserDetailsService et JWTService
    private  JWTService jwtService;

    @PostMapping("/api/auth/login") // Mappe cette méthode à l'URL "/api/auth/login" pour les requêtes POST
    public ResponseEntity<?> createAuthenticationToken(@RequestBody USERS users) throws Exception {
        try {
            // Tente d'authentifier l'utilisateur avec l'email et le mot de passe fournis
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(users.getEmail(), users.getPassword())
            );
        } catch (AuthenticationException e) {
            // Si l'authentification échoue, crée une map avec un message d'erreur
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put( "message: ","error");

            // Retourne la map sous format JSON avec un statut HTTP 401 (Non autorisé)
            return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
        }

        // Charge les détails de l'utilisateur à partir de l'email fourni
        final UserDetails userDetails = userDetailsService.loadUserByUsername(users.getEmail());
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
}