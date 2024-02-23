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

@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;
    @Autowired
    private CustomUserDetailsService customUserDetailsService; 


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        System.out.println("JwtAuthenticationTokenFilter a été appelé pour la requête : " + request.getRequestURI());        
        String authHeader = request.getHeader("Authorization");

System.out.println("Valeur de authHeader : " + authHeader); // Point de contrôle

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7); // Le token est après "Bearer "
            String username = jwtService.getUserEmailFromToken(authToken);
            System.out.println("username décodé dans JwtAuthenticationTokenFilter : " + username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.customUserDetailsService.loadUserByUsername(username);

                System.out.println("Valeur de userDetails : " + userDetails); // Point de contrôle

                if (jwtService.validateToken(authToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // Afficher le UsernamePasswordAuthenticationToken dans la console
                    System.out.println("UsernamePasswordAuthenticationToken : " + authentication.toString());
                }
            }
        }

        chain.doFilter(request, response);
    }
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
    return request.getServletPath().equals("/api/auth/login") || request.getServletPath().equals("/api/auth/register");
}
}