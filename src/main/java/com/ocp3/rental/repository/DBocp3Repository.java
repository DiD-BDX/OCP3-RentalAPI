package com.ocp3.rental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ocp3.rental.model.USERS;

// Définition de l'interface DBUserRepository qui étend JpaRepository
// JpaRepository est une interface Spring Data JPA qui fournit des méthodes CRUD génériques
// USERS est l'entité qui représente la table des utilisateurs dans la base de données
// Integer est le type de l'identifiant de l'entité USERS
public interface DBocp3Repository extends JpaRepository<USERS, Integer> {
    // Déclaration de la méthode findByEmail
    // Spring Data JPA générera automatiquement l'implémentation de cette méthode
    // Elle retournera un utilisateur en fonction de son email
    public Optional<USERS> findByEmail(String email);

    // Déclaration de la méthode findByName
    // Spring Data JPA générera automatiquement l'implémentation de cette méthode
    // Elle retournera un utilisateur en fonction de son nom
    public USERS findByName(String name);
}