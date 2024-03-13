package com.ocp3.rental.controller;

import org.springframework.web.bind.annotation.RestController;
import com.ocp3.rental.DTO.UsersDataTransferObject;
import com.ocp3.rental.model.UsersEntity;
import com.ocp3.rental.repository.DBocp3Repository;
import com.ocp3.rental.service.JWTService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;



@RestController
public class UsersController {
    
    @Autowired
    private DBocp3Repository dbocp3Repository;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/api/auth/me") // Mappe cette méthode à l'URL "/api/auth/me" pour les requêtes GET
    @Operation(summary = "Account of the logged user", security = {
        @SecurityRequirement(name = "bearerToken")})
    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User account with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\n" + //
                                                "\t\"name\": \"Test TEST\",\n" + //
                                                "\t\"email\": \"test@test.com\",\n" + //
                                                "\t\"created_at\": \"2022/02/02\",\n" + //
                                                "\t\"updated_at\": \"2022/08/02\"  \n" + //
                                                "}")
        })),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{}")
        }))
    })
    public ResponseEntity<?> me(HttpServletRequest request) {
        // Récupère le token du header de la requête
        String token = request.getHeader("Authorization").substring(7); // Supprime "Bearer "
        // Récupère l'email de l'utilisateur à partir du token
        String email = jwtService.getUserEmailFromToken(token);
        // Récupère l'utilisateur à partir de l'email
        UsersEntity user = dbocp3Repository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Crée une map avec les informations de l'utilisateur
        Map<String, Object> userData = Map.of(
            "name", user.getName(),
            "email", user.getEmail(),
            "created_at", user.getCreatedAt(),
            "updated_at", user.getUpdatedAt()
        );

        // Retourne les informations de l'utilisateur sous format JSON
        return ResponseEntity.ok(userData);
    }

    @GetMapping("/api/user/{id}") // Mappe cette méthode à l'URL "/api/user/{id}" pour les requêtes GET
    @Operation(summary = "Account of an user by Id", security = {
        @SecurityRequirement(name = "bearerToken")})
    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User account with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\n" + //
                                                "\t\"id\": \"2\",\n" + //
                                                "\t\"name\": \"Owner Name\",\n" + //
                                                "\t\"email\": \"test@test.com\",\n" + //
                                                "\t\"created_at\": \"2022/02/02\",\n" + //
                                                "\t\"updated_at\": \"2022/08/02\"  \n" + //
                                                "}")
        })),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "")
        }))
    })
    public ResponseEntity<UsersDataTransferObject> getUserEntity(@PathVariable Integer id) {
        // Récupère l'utilisateur avec l'ID spécifié dans la base de données
        UsersEntity userData = dbocp3Repository.findById(id).get();
        // Crée un nouvel objet UsersDataTransferObject
        UsersDataTransferObject userDto = new UsersDataTransferObject();
        // Remplit l'objet UsersDataTransferObject avec les données de l'utilisateur
        userDto.setId(userData.getId());
        userDto.setName(userData.getName());
        userDto.setEmail(userData.getEmail());
        userDto.setCreated_at(userData.getCreatedAt());
        userDto.setUpdated_at(userData.getUpdatedAt());

        // Retourne l'objet UsersDataTransferObject sous format JSON
        return ResponseEntity.ok(userDto);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class) // Cette méthode est appelée quand une exception HttpMessageNotReadableException est levée
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
        // Crée un nouvel objet HttpHeaders et définit le type de contenu à JSON
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        // Retourne une réponse avec un corps vide et un statut HTTP 400 (Bad Request)
        return new ResponseEntity<>("{}", headers, HttpStatus.BAD_REQUEST);
    }
}
