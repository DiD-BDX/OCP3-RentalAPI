package com.ocp3.rental.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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


@Service
public class JWTService {

	@Autowired
	private CustomUserDetailsService customUserDetailsService;

    private JwtEncoder jwtEncoderApi;
    private JwtDecoder jwtDecoderApi;

    
    public JWTService(JwtEncoder jwtEncoderApi, JwtDecoder jwtDecoderApi) {
        this.jwtEncoderApi = jwtEncoderApi;
        this.jwtDecoderApi = jwtDecoderApi;
    }

    public String generateToken(Authentication authentication) {
		String userEmail = authentication.getName();
		UserDetails userDetails = customUserDetailsService.loadUserByUsername(userEmail);
		String email = userDetails.getUsername();
        Instant now = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.DAYS))
                .subject(email)
                .build();
		JwtEncoderParameters jwtEncoderParameters = JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims);
		Jwt jwt = this.jwtEncoderApi.encode(jwtEncoderParameters);

		// Afficher le token en clair dans la console
		Jwt jwtdecode = jwtDecoderApi.decode(jwt.getTokenValue());
		System.out.println("Email décodé dans generateToken : " + jwtdecode.getClaimAsString("sub"));

		return jwt.getTokenValue();
    }

    public String getUserEmailFromToken(String token) {
        Jwt jwt = jwtDecoderApi.decode(token);
		System.out.println("Email décodé de GetUserEmailFromToken : " + jwt.getClaimAsString("sub"));
        return jwt.getClaimAsString("sub"); // "sub" est l'email dans le JWT
    }

    public boolean validateToken(String token, UserDetails userDetails) {
    Jwt jwt = jwtDecoderApi.decode(token);
	// Afficher le token décodé dans la console
    System.out.println("Email décodé dans validateToken : " + jwt.getClaimAsString("sub"));
    String email = userDetails.getUsername();
	System.out.println("Email de l'utilisateur : " + email);
    return jwt.getClaimAsString("sub").equals(email) && !JwtValidators.createDefault().validate(jwt).hasErrors();
	}
}