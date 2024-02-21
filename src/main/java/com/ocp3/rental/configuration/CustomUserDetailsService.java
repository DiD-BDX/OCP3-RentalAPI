package com.ocp3.rental.configuration;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBUserRepository;


// Cette classe fournit un moyen de charger les détails d'un utilisateur à partir de la base de données
// pour l'authentification avec Spring Security. Elle attribue également une autorité de base "ROLE_USER" à tous 
// les utilisateurs.

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	private DBUserRepository dbUserRepository;

	@Override
	public UserDetails loadUserByUsername(String useremail) throws UsernameNotFoundException {
		USERS userByEmail = dbUserRepository.findByEmail(useremail);
		
		return new User(userByEmail.getEmail(), userByEmail.getPassword(), getGrantedAuthorities());
	}

	private List<GrantedAuthority> getGrantedAuthorities() {
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		return authorities;
	}
}