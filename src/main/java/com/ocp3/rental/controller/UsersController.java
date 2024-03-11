package com.ocp3.rental.controller;

import org.springframework.web.bind.annotation.RestController;
import com.ocp3.rental.DTO.UsersDataTransferObject;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBocp3Repository;
import com.ocp3.rental.service.JWTService;

import jakarta.servlet.http.HttpServletRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class UsersController {

    @Autowired
    private DBocp3Repository dbocp3Repository;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/api/auth/me")
    public ResponseEntity<?> me(HttpServletRequest request) {
        // Récupère l'utilisateur à partir de la requête
        // Récupère le token du header de la requête
        String token = request.getHeader("Authorization").substring(7); // Supprime "Bearer "
        // Récupère l'email de l'utilisateur à partir du token
        String email = jwtService.getUserEmailFromToken(token);
        // Récupère l'utilisateur à partir de l'email
        Optional<USERS> existingUsers = dbocp3Repository.findByEmail(email);

        // Crée une map avec les informations de l'utilisateur
        Map<String, Object> userData = new LinkedHashMap<>();

        if (existingUsers.isPresent()) {
            USERS user = existingUsers.get();
            
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("created_at", user.getCreatedAt());
            userData.put("updated_at", user.getUpdatedAt());   
        } else {
            
        }
        // Retourne les informations de l'utilisateur sous format JSON
        return ResponseEntity.ok(userData);
    }

    @GetMapping("/api/user/{id}")
    public ResponseEntity<UsersDataTransferObject> getUserEntity(@PathVariable Integer id) {
        USERS userData = dbocp3Repository.findById(id).get();
        UsersDataTransferObject userDto = new UsersDataTransferObject();
        userDto.setId(userData.getId());
        userDto.setName(userData.getName());
        userDto.setEmail(userData.getEmail());
        userDto.setCreated_at(userData.getCreatedAt());
        userDto.setUpdated_at(userData.getUpdatedAt());

    return ResponseEntity.ok(userDto);
    }
    

}
