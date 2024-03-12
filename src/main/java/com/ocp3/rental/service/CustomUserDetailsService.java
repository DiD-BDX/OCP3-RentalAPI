package com.ocp3.rental.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.ocp3.rental.DTO.UsersDataTransferObject;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBocp3Repository;


// Cette classe fournit un moyen de charger les détails d'un utilisateur à partir de la base de données
// pour l'authentification avec Spring Security. Elle attribue également une autorité de base "ROLE_USER" à tous 
// les utilisateurs.

@Service // Indique que cette classe est un service Spring
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired // Injection de dépendances pour DBUserRepository
    private DBocp3Repository dbocp3Repository;

    @Override // Surcharge de la méthode loadUserByUsername de l'interface UserDetailsService
    public UserDetails loadUserByUsername(String useremail) throws UsernameNotFoundException {
        // Recherche un utilisateur par son email dans la base de données
        Optional<USERS> userByEmail = dbocp3Repository.findByEmail(useremail);
        if (userByEmail.isPresent()) {
            USERS user = userByEmail.get();
            return new User(user.getEmail(), user.getPassword(), getGrantedAuthorities());
        } else {
            throw new UsernameNotFoundException("User not found with email: " + useremail);
        }
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


    public USERS registerUser(UsersDataTransferObject usersDto) {
        // Crée une nouvelle instance de l'entité USERS
        USERS users = new USERS();
        // Remplit l'entité USERS avec les données de l'objet usersDto
        users.setEmail(usersDto.getEmail());
        users.setPassword(usersDto.getPassword());
        users.setName(usersDto.getName());
        users.setCreatedAt(usersDto.getCreated_at());
        users.setUpdatedAt(usersDto.getUpdated_at());
        // Enregistre l'entité USERS dans la base de données et retourne l'entité enregistrée
        return dbocp3Repository.save(users);
    }
}