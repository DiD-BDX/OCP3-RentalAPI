package com.ocp3.rental.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService; 

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {       
        // Récupère le header "Authorization" de la requête
        String authHeader = request.getHeader("Authorization");
        
        // Vérifie si le header "Authorization" existe et commence par "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Récupère le token à partir du header "Authorization"
            String authToken = authHeader.substring(7);
            // Récupère le nom d'utilisateur à partir du token
            String username = jwtService.getUserEmailFromToken(authToken);

            // Vérifie si le nom d'utilisateur existe et si l'utilisateur n'est pas déjà authentifié
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Récupère les détails de l'utilisateur à partir du nom d'utilisateur
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

                // Vérifie si le token est valide
                if (jwtService.validateToken(authToken, userDetails)) {
                    // Crée un objet d'authentification à partir des détails de l'utilisateur
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    // Définit les détails de l'authentification à partir de la requête
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Définit l'authentification dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } else if (request.getMethod().equals("GET") && request.getRequestURI().equals("/api/auth/me")) {
            // Si la requête est une requête GET à l'URL "/api/auth/me", définit le statut de la réponse à 401 (Non autorisé)
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            // Définit le type de contenu de la réponse à JSON
            response.setContentType("application/json");
            // Écrit un corps vide dans la réponse
            response.getWriter().write("{}");
            return;
        }

        // Passe la requête et la réponse au prochain filtre dans la chaîne
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Récupère le chemin de la requête
        String path = request.getServletPath();
        // Vérifie si le chemin correspond à l'un des chemins spécifiés
        return path.equals("/api/auth/login")
            || path.equals("/api/auth/register")
            || path.startsWith("/swagger-ui")
            || path.startsWith("/v3/")
            || PathRequest.toStaticResources().atCommonLocations().matches(request);
    }
}