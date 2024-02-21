/* package com.ocp3.rental.controller;

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
	
	// cette classe fournit un endpoint API pour l'authentification des utilisateurs. 
	// Lorsqu'un utilisateur s'authentifie avec succès, un token JWT est généré et renvoyé, 
	// qui peut ensuite être utilisé pour authentifier les requêtes ultérieures de l'utilisateur.
	@PostMapping("/api/auth/login")
	public String getToken(Authentication authentication) {
        		String token = jwtService.generateToken(authentication);
        		return token;
	}
	
}
 */