package com.ocp3.rental.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.service.JWTService;

@RestController
public class LoginController {
	private JWTService jwtService;

	public LoginController(JWTService jwtService) {
		this.jwtService = jwtService;
	}
	
	@PostMapping("/api/auth/login")
	public String getToken(Authentication authentication) {
        		String token = jwtService.generateToken(authentication);
        		return token;
	}
	
}