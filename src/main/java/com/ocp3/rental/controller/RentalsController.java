package com.ocp3.rental.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.DTO.RentalsDataTransferObject;
import com.ocp3.rental.model.RENTALS;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBRentalsRepository;
import com.ocp3.rental.service.JWTService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
@RequestMapping()
public class RentalsController {

    @Autowired
    private DBRentalsRepository dbRentalsRepository;

    @Autowired
    private JWTService jwtService;

    @PostMapping({"/api/rentals", "/api/rentals/**"})
    public ResponseEntity<?> rentals(HttpServletRequest request, @ModelAttribute RentalsDataTransferObject rentalsDto) throws IOException {

    // Récupère l'utilisateur authentifié à partir de la requête
        USERS users = jwtService.getUserFromRequest(request);
        String authenticatedUser = users.getName();
        System.out.println("Authenticated user: " + authenticatedUser);
        Integer authenticatedUserId = users.getId();
        System.out.println("ID authenticated: " + authenticatedUserId);
    
    // valeurs de rentals dans la console
    System.out.println("Name: " + rentalsDto.getName());
    System.out.println("Surface: " + rentalsDto.getSurface());
    System.out.println("Price: " + rentalsDto.getPrice());
    System.out.println("Picture: " + rentalsDto.getPicture());
    System.out.println("Description: " + rentalsDto.getDescription());
        
    // Crée un nouvel objet RENTALS à partir du DTO
    RENTALS rentals = new RENTALS();

    rentals.setName(rentalsDto.getName());
    rentals.setSurface(rentalsDto.getSurface());
    rentals.setPrice(rentalsDto.getPrice());
    rentals.setPicture(rentalsDto.getPicture());
    rentals.setOwnerId(authenticatedUserId);
    rentals.setDescription(rentalsDto.getDescription());
    rentals.setCreatedAt(LocalDate.now());
    rentals.setUpdatedAt(LocalDate.now());

    // Sauvegarde l'objet RENTALS dans la base de données
    dbRentalsRepository.save(rentals);

    // Crée une map avec le message de succès
    Map<String, String> response = new HashMap<>();
    response.put("message", "Rental created !");

    System.out.println("Returning response...");
    return ResponseEntity.ok(response);
    }

    @GetMapping({"/api/rentals", "/api/rentals/"})
    public ResponseEntity<Map<String, List<RENTALS>>> getRentals() { 
    List<RENTALS> rentals = dbRentalsRepository.findAll();
    Map<String, List<RENTALS>> response = new HashMap<>();
    response.put("rentals", rentals);
    return ResponseEntity.ok(response);
    }

    @GetMapping({"/api/rentals/{id}", "/api/rentals/{id}/"})
    public ResponseEntity<RENTALS> getRental(@PathVariable Integer id) {
    RENTALS rental = dbRentalsRepository.findById(id).get();
    System.out.println("Returning rental..." + rental);
    return ResponseEntity.ok(rental);
    }
    
    @PutMapping("/api/rentals/{id}")
    public ResponseEntity<?> updateRental(@PathVariable Integer id, @ModelAttribute RentalsDataTransferObject rentalsDto) {
    RENTALS rental = dbRentalsRepository.findById(id).get();
    rental.setName(rentalsDto.getName());
    rental.setSurface(rentalsDto.getSurface());
    rental.setPrice(rentalsDto.getPrice());
    // rental.setPicture(rentalsDto.getPicture());
    rental.setDescription(rentalsDto.getDescription());
    rental.setUpdatedAt(LocalDate.now());
    dbRentalsRepository.save(rental);

    // Crée une map avec le message de succès
    Map<String, String> response = new HashMap<>();
    response.put("message", "Rental updated !");

    System.out.println("Returning response...");
    return ResponseEntity.ok(response);
    }
}
