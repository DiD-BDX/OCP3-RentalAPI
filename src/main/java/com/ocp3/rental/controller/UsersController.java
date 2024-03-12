package com.ocp3.rental.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.ocp3.rental.DTO.LoginUserDataTransferObject;
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
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    @PostMapping("/api/auth/register")
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
        // Récupère le token du header de la requête
        String token = request.getHeader("Authorization").substring(7); // Supprime "Bearer "
        // Récupère l'email de l'utilisateur à partir du token
        String email = jwtService.getUserEmailFromToken(token);
        // Récupère l'utilisateur à partir de l'email
        USERS user = dbocp3Repository.findByEmail(email)
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

    @Hidden
    @GetMapping("/api/user/{id}") // Mappe cette méthode à l'URL "/api/user/{id}" pour les requêtes GET
    public ResponseEntity<UsersDataTransferObject> getUserEntity(@PathVariable Integer id) {
        // Récupère l'utilisateur avec l'ID spécifié dans la base de données
        USERS userData = dbocp3Repository.findById(id).get();
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
