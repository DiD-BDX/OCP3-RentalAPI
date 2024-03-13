package com.ocp3.rental.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.stereotype.Service;

import com.ocp3.rental.model.UsersEntity;
import com.ocp3.rental.repository.DBocp3Repository;

import jakarta.servlet.http.HttpServletRequest;

@Service // Indique que cette classe est un service Spring
public class JWTService {

    @Autowired // Injection de dépendances pour CustomUserDetailsService
    private CustomUserDetailsService customUserDetailsService;

    @Autowired // Injection de dépendances pour AuthenticationManager, UserDetailsService et JWTService
    private  UserDetailsService userDetailsService;
    
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
        // Récupère les détails de l'utilisateur à partir de l'objet d'authentification
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(authentication.getName());
    
        // Obtient l'heure actuelle
        Instant now = Instant.now();
    
        // Construit un ensemble de revendications JWT avec l'émetteur, l'heure d'émission, l'heure d'expiration (24 heures plus tard) et le sujet (nom d'utilisateur)
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .subject(userDetails.getUsername())
                .build();
    
        // Construit les paramètres de l'encodeur JWT à partir de l'en-tête JWS et de l'ensemble de revendications
        JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
    
        // Encode l'ensemble de revendications en un token JWT
        Jwt jwt = this.jwtEncoderApi.encode(jwtEncoderParameters);
    
        // Retourne la valeur du token JWT
        return jwt.getTokenValue();
    }

    // Obtient l'email de l'utilisateur à partir du token JWT
    public String getUserEmailFromToken(String token) {
        // Décode le token JWT
        Jwt jwt = jwtDecoderApi.decode(token);
        // Récupère l'email de l'utilisateur à partir de la revendication "sub" du token JWT
        return jwt.getClaimAsString("sub");
    }
    
    public Integer getUserIdFromToken(String token) {
        // Récupère l'email de l'utilisateur à partir du token
        String email = getUserEmailFromToken(token);
        // Récupère l'utilisateur à partir de l'email
        UsersEntity user = dbocp3Repository.findByEmail(email)
            // Si l'utilisateur n'est pas trouvé, lance une exception
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
        // Retourne l'ID de l'utilisateur
        return user.getId();
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        // Décode le token JWT
        Jwt jwt = jwtDecoderApi.decode(token);
        // Récupère l'email de l'utilisateur à partir des détails de l'utilisateur
        String email = userDetails.getUsername();
        // Vérifie si l'email de l'utilisateur correspond à la revendication "sub" du token JWT et si le token est valide
        return jwt.getClaimAsString("sub").equals(email) && !JwtValidators.createDefault().validate(jwt).hasErrors();
    }

    public UsersEntity getUserFromRequest(HttpServletRequest request) {
        // Récupère le token du header de la requête
        String token = request.getHeader("Authorization").substring(7); // Supprime "Bearer "
        // Récupère l'email de l'utilisateur à partir du token
        String email = getUserEmailFromToken(token);
        // Récupère l'utilisateur à partir de l'email
        return dbocp3Repository.findByEmail(email)
            // Si l'utilisateur n'est pas trouvé, lance une exception
            .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
    
    public ResponseEntity<?> GenerateTokenMapFromUser(String email) {
        // Récupère les détails de l'utilisateur à partir de l'email
        final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        // Crée un objet d'authentification à partir du nom d'utilisateur
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null);
        // Génère un token à partir de l'objet d'authentification
        final String token = generateToken(authentication);
        // Crée une map avec le token JWT
        Map<String, String> tokenMap = Map.of("token", token);
        // Retourne la map sous format JSON
        return ResponseEntity.ok(tokenMap);
    }   
}