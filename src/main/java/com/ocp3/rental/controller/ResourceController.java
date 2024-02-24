package com.ocp3.rental.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBocp3Repository;
import com.ocp3.rental.service.JWTService;

import jakarta.servlet.http.HttpServletRequest;


@RestController
@RequestMapping("/api/auth")
public class ResourceController {
    @Autowired
    private DBocp3Repository dbocp3Repository;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        // Récupère l'utilisateur à partir de la requête
        USERS users = getUserFromRequest(request);

        // Crée une map avec les informations de l'utilisateur
        Map<String, Object> userData = new LinkedHashMap<>();
        userData.put("name", users.getName());
        userData.put("email", users.getEmail());
        userData.put("created_at", users.getCreated_at());
        userData.put("updated_at", users.getUpdated_at());

        // Retourne les informations de l'utilisateur sous format JSON
        return ResponseEntity.ok(userData);
    }

    private USERS getUserFromRequest(HttpServletRequest request) {
        // Récupère le token du header de la requête
        String token = request.getHeader("Authorization").substring(7); // Supprime "Bearer "
        // Récupère l'email de l'utilisateur à partir du token
        String email = jwtService.getUserEmailFromToken(token);
        // Récupère l'utilisateur à partir de l'email
        return dbocp3Repository.findByEmail(email);
    }
}