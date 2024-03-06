package com.ocp3.rental.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.stereotype.Service;

import com.ocp3.rental.configuration.CustomUserDetailsService;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBocp3Repository;

import jakarta.servlet.http.HttpServletRequest;

@Service // Indique que cette classe est un service Spring
public class JWTService {

    @Autowired // Injection de dépendances pour CustomUserDetailsService
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private DBocp3Repository dbocp3Repository;

    private JwtEncoder jwtEncoderApi; // Pour encoder les JWT
    private JwtDecoder jwtDecoderApi; // Pour décoder les JWT

    // Constructeur avec injection de dépendances pour JwtEncoder et JwtDecoder
    public JWTService(JwtEncoder jwtEncoderApi, JwtDecoder jwtDecoderApi) {
        this.jwtEncoderApi = jwtEncoderApi;
        this.jwtDecoderApi = jwtDecoderApi;
    }

    // Génère un token JWT pour une authentification donnée
    public String generateToken(Authentication authentication) {
        String userEmail = authentication.getName(); // Obtient le nom d'utilisateur (email)
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail); // Charge les détails de l'utilisateur
        String email = userDetails.getUsername(); // Obtient l'email de l'utilisateur
        Instant now = Instant.now(); // Obtient l'heure actuelle
        // Construit les revendications JWT
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self") // L'émetteur du token
                .issuedAt(now) // L'heure d'émission du token
                .expiresAt(now.plus(1, ChronoUnit.DAYS)) // L'heure d'expiration du token (1 jour plus tard)
                .subject(email) // Le sujet du token (l'email de l'utilisateur)
                .build();
        // Paramètres pour l'encodage du JWT
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
        Jwt jwt = this.jwtEncoderApi.encode(jwtEncoderParameters); // Encode le JWT
        return jwt.getTokenValue(); // Retourne la valeur du token JWT
    }

    // Obtient l'email de l'utilisateur à partir du token JWT
    public String getUserEmailFromToken(String token) {
        Jwt jwt = jwtDecoderApi.decode(token); // Décode le token JWT
        return jwt.getClaimAsString("sub"); // Retourne la revendication "sub" (l'email de l'utilisateur) en tant que chaîne
    }

    // Valide le token JWT
    public boolean validateToken(String token, UserDetails userDetails) {
    Jwt jwt = jwtDecoderApi.decode(token); // Décode le token JWT
    String email = userDetails.getUsername(); // Obtient l'email de l'utilisateur
    System.out.println("Email de l'utilisateur : " + email); // Affiche l'email de l'utilisateur
    // Vérifie si la revendication "sub" est égale à l'email de l'utilisateur et si le token JWT n'a pas d'erreurs
    return jwt.getClaimAsString("sub").equals(email) && !JwtValidators.createDefault().validate(jwt).hasErrors();
    }

    public USERS getUserFromRequest(HttpServletRequest request) {
        // Récupère le token du header de la requête
        String token = request.getHeader("Authorization").substring(7); // Supprime "Bearer "
        // Récupère l'email de l'utilisateur à partir du token
        String email = getUserEmailFromToken(token);
        // Récupère l'utilisateur à partir de l'email
        Optional<USERS> existingUser = dbocp3Repository.findByEmail(email);
        return existingUser.get();
    }
}