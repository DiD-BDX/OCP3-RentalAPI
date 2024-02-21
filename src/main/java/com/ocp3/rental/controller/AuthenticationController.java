package com.ocp3.rental.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.configuration.CustomUserDetailsService;
import com.ocp3.rental.model.AuthenticationResponse;
import com.ocp3.rental.model.LoginRequest;
import com.ocp3.rental.service.JWTService;

@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JWTService jwtservice;
    private final CustomUserDetailsService userDetailsService;

    
    public AuthenticationController(AuthenticationManager authenticationManager, JWTService jwtservice, CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtservice = jwtservice;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/api/auth/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        final String jwt = jwtservice.generateToken(authentication);

        return ResponseEntity.ok(new AuthenticationResponse(jwt));
    }
}