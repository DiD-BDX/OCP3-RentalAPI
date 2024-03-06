package com.ocp3.rental.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ocp3.rental.configuration.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component // Indique que cette classe est un composant Spring
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired // Injection de dépendances pour JWTService
    private JWTService jwtService;
    @Autowired // Injection de dépendances pour CustomUserDetailsService
    private CustomUserDetailsService customUserDetailsService; 

    // Cette méthode est appelée pour chaque requête HTTP
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {       
        // Récupère le header "Authorization" de la requête
        String authHeader = request.getHeader("Authorization");
        
        // Vérifie si le header "Authorization" existe et commence par "Bearer "
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Récupère le token JWT du header "Authorization"
            String authToken = authHeader.substring(7); // Le token est après "Bearer "
            // Récupère l'email de l'utilisateur à partir du token JWT
            String username = jwtService.getUserEmailFromToken(authToken);

            // Vérifie si l'email de l'utilisateur existe et si l'utilisateur n'est pas déjà authentifié
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Charge les détails de l'utilisateur à partir de l'email
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

                // Vérifie si le token JWT est valide
                if (jwtService.validateToken(authToken, userDetails)) {
                    // Crée une authentification à partir des détails de l'utilisateur
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    System.out.println("Authentication : " + authentication);
                    // Ajoute les détails de la requête à l'authentification
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    // Enregistre l'authentification dans le contexte de sécurité
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("SecurityContextHolder.getContext().getAuthentication() : " + SecurityContextHolder.getContext().getAuthentication());
                }
            }
        } else if (request.getMethod().equals("GET") && request.getRequestURI().equals("/api/auth/me")) {
            // Si la condition if n'est pas remplie, mais que la requête est une requête GET sur /api/auth/me,
            // renvoie une réponse HTTP 401 avec un corps JSON vide
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{}");
            return;
        }
         // Vérifie si le header Authorization est différent de Bearer jwt
         String authorizationHeader = request.getHeader("Authorization");
         if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
             response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
             return;
         }

        // Passe la requête et la réponse au prochain filtre dans la chaîne
        chain.doFilter(request, response);
    }

    // Cette méthode détermine si le filtre doit être appliqué ou non à la requête
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Le filtre ne doit pas être appliqué si la requête est pour "/api/auth/login" ou "/api/auth/register"
        return request.getServletPath().equals("/api/auth/login") || request.getServletPath().equals("/api/auth/register");
    }
}