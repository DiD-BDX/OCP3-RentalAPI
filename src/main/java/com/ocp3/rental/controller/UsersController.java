package com.ocp3.rental.controller;

import org.springframework.web.bind.annotation.RestController;
import com.ocp3.rental.DTO.UsersDataTransferObject;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBocp3Repository;
import com.ocp3.rental.service.CustomUserDetailsService;
import com.ocp3.rental.service.JWTService;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
public class UsersController {

    @Autowired
    private CustomUserDetailsService customUserDetailsService; // Référence vers le service de gestion des utilisateurs

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private DBocp3Repository dbocp3Repository;

    @Autowired // Injection de dépendances pour AuthenticationManager, UserDetailsService et JWTService
    private  AuthenticationManager authManager;

    @Autowired
    private JWTService jwtService;

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
    @PostMapping("/api/auth/login") // Mappe cette méthode à l'URL "/api/auth/login" pour les requêtes POST
    public ResponseEntity<?> createAuthenticationToken(@RequestBody Map<String, Object> body) throws Exception {
        String email = (String) body.get("email");
        String password = (String) body.get("password");
        try {
            // Tente d'authentifier l'utilisateur avec l'email et le mot de passe fournis
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            // Si l'authentification échoue, crée une map avec un message d'erreur
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put( "message: ","error");

            // Retourne la map sous format JSON avec un statut HTTP 401 (Non autorisé)
            return new ResponseEntity<>(responseBody, HttpStatus.UNAUTHORIZED);
        }

        // Affiche le token à partir de l'email fourni
        return jwtService.GenerateTokenMapFromUser(email);
        
    }

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
    @PostMapping("/api/auth/register") // Mappe cette méthode à l'URL "/api/auth/register" pour les requêtes POST
    public ResponseEntity<?> register(@RequestBody UsersDataTransferObject usersDto) { // Prend en paramètre un objet usersDto qui est automatiquement converti à partir du corps de la requête HTTP
        System.out.println("Starting register method...");
        System.out.println(usersDto);
        
        // Vérifie si un utilisateur avec le même email existe déjà
        Optional<USERS> existingUser = dbocp3Repository.findByEmail(usersDto.getEmail());
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

        return jwtService.GenerateTokenMapFromUser(usersDto.getEmail());
    }

    @Operation(summary = "Account of the logged user", security = {
        @SecurityRequirement(name = "bearerToken")})
    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User account with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\n" + //
                                                "  \"id\": 1,\n" + //
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

    @Hidden
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
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>("{}", headers, HttpStatus.BAD_REQUEST);
    }
}
