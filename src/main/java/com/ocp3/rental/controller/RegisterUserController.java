package com.ocp3.rental.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ocp3.rental.DTO.UsersDataTransferObject;
import com.ocp3.rental.repository.DBocp3Repository;
import com.ocp3.rental.service.CustomUserDetailsService;
import com.ocp3.rental.service.JWTService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
public class RegisterUserController {
    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Référence vers le service de gestion des utilisateurs

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private DBocp3Repository dbocp3Repository;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/api/auth/register") // Mappe cette méthode à l'URL "/api/auth/register" pour les requêtes POST
    @Operation(summary = "Register a user", security = {
        @SecurityRequirement(name = "bearerToken")})
    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\n" + //
                                                "  \"token\": \"jwt\"\n" + //
                                                "}")
        })),
        @ApiResponse(responseCode = "400", description = "bad Request",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{}")
        }))
    })
    public ResponseEntity<?> register(@RequestBody UsersDataTransferObject usersDto) {
        // Vérifie si un utilisateur avec le même email existe déjà
        dbocp3Repository.findByEmail(usersDto.getEmail())
            .ifPresent(user -> {
                // Si c'est le cas, renvoie une exception
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Existing user with this Email");
            });

        usersDto.setCreated_at(LocalDate.now());
        usersDto.setUpdated_at(LocalDate.now());

        // Encodage du mot de passe avant enregistrement dans la base de données
        usersDto.setPassword(passwordEncoder.encode(usersDto.getPassword()));

        // Crée une nouvelle entité USERS et l'enregistre dans la base de données
        customUserDetailsService.registerUser(usersDto);

        return jwtService.GenerateTokenMapFromUser(usersDto.getEmail());
    }

}
