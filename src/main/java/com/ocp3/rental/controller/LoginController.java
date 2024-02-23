package com.ocp3.rental.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.ocp3.rental.model.LoginRequest;
import com.ocp3.rental.service.JWTService;


@RestController
public class LoginController {
    @Autowired
    private  AuthenticationManager authManager;
    @Autowired
    private  UserDetailsService userDetailsService;
    @Autowired
    private  JWTService jwtService;

    
	
	@PostMapping("/api/auth/login")
	public ResponseEntity<?> createAuthenticationToken(@RequestBody LoginRequest loginRequest) throws Exception {
        try {
            authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            throw new Exception("Incorrect username or password", e);
        }

        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getEmail());
        final Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(), null);
        System.out.println("Username: " + authentication.getName());
        final String token = jwtService.generateToken(authentication);
        Map<String, String> tokenMap = new HashMap<String, String>();
        tokenMap.put("token", token);
        return ResponseEntity.ok(tokenMap);
    }
	
}