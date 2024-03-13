package com.ocp3.rental.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.DTO.LoginUserDataTransferObject;
import com.ocp3.rental.service.JWTService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
public class LoginUserController {

    @Autowired // Injection de dépendances pour AuthenticationManager, UserDetailsService et JWTService
    private  AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

    @PostMapping("/api/auth/login") // Mappe cette méthode à l'URL "/api/auth/login" pour les requêtes POST
    @Operation(summary = "Login in the API", security = {
        @SecurityRequirement(name = "bearerToken")})

    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User logged in with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\n" + //
                                                "  \"token\": \"jwt\"\n" + //
                                                "}")
        })),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\n" + //
                                                "  \"message\": \"error\"\n" + //
                                                "}")
        }))
    })
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginUserDataTransferObject usersDto) {
        String email = usersDto.getEmail();
        String password = usersDto.getPassword();

        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(email, password);
        try {
            // Tente d'authentifier l'utilisateur avec l'email et le mot de passe fournis
            authManager.authenticate(authenticationToken);
        } catch (AuthenticationException e) {
            // Si l'authentification échoue, crée une map avec un message d'erreur
            Map<String, String> responseBody = Map.of("message", "error");

            // Retourne la map sous format JSON avec un statut HTTP 401 (Non autorisé)
            return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
        }

        // Affiche le token à partir de l'email fourni
        return jwtService.GenerateTokenMapFromUser(email);
    }

}
