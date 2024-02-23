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

@Service // Indique que cette classe est un service Spring
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired // Injection de dépendances pour DBUserRepository
    private DBUserRepository dbUserRepository;

    @Override // Surcharge de la méthode loadUserByUsername de l'interface UserDetailsService
    public UserDetails loadUserByUsername(String useremail) throws UsernameNotFoundException {
        // Recherche un utilisateur par son email dans la base de données
        USERS userByEmail = dbUserRepository.findByEmail(useremail);
        
        // Retourne un nouvel objet User (implémentation de UserDetails) avec l'email, le mot de passe et les autorités de l'utilisateur
        return new User(userByEmail.getEmail(), userByEmail.getPassword(), getGrantedAuthorities());
    }

    // Méthode privée pour obtenir les autorités accordées à l'utilisateur
    private List<GrantedAuthority> getGrantedAuthorities() {
        // Crée une nouvelle liste d'autorités
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        // Ajoute l'autorité "ROLE_USER" à la liste
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        // Retourne la liste d'autorités
        return authorities;
    }
}