package com.ocp3.rental.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ocp3.rental.DTO.RentalsDataTransferObject;
import com.ocp3.rental.model.RENTALS;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBRentalsRepository;
import com.ocp3.rental.service.ApiServices;
import com.ocp3.rental.service.JWTService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;


@RestController
public class RentalsController {
    @Value("${base.picture.path}")
    private String basePicturePath;
    
    @Autowired
    private DBRentalsRepository dbRentalsRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApiServices apiServices;

    @PostMapping("/api/rentals")
    public ResponseEntity<?> rentals(HttpServletRequest request,
        @ModelAttribute RentalsDataTransferObject rentalsDto,
        @RequestParam("picture") MultipartFile image) throws IOException {

        // Récupère l'utilisateur authentifié à partir de la requête
        USERS users = jwtService.getUserFromRequest(request);
        String authenticatedUser = users.getName();
        System.out.println("Authenticated user: " + authenticatedUser);
        Integer authenticatedUserId = users.getId();
        System.out.println("ID authenticated: " + authenticatedUserId);

        // Enregistre l'image et récupère le chemin du fichier
        String pictureName = apiServices.savePictureFromRequest(image, request);
        System.out.println("Picture name: " + pictureName);
        
        // Crée un nouvel objet RENTALS à partir du DTO
        RENTALS rentals = new RENTALS();
        rentalsDto.setPictureUrl(basePicturePath + pictureName);

        rentals.setName(rentalsDto.getName());
        rentals.setSurface(rentalsDto.getSurface());
        rentals.setPrice(rentalsDto.getPrice());
        rentals.setPicture(rentalsDto.getPictureUrl());
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

    @GetMapping("/api/rentals")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getRentals(HttpServletRequest request) {
        List<RENTALS> rentalsList = dbRentalsRepository.findAll();

        // Recupère les valeurs de l'utilisateur authentifié
        String token = request.getHeader("Authorization").substring(7);
        Integer authenticatedUserId = jwtService.getUserIdFromToken(token);
        System.out.println("Authenticated user ID: " + authenticatedUserId);

        List<Map<String, Object>> rentalsDtoList = new ArrayList<>();

        for (RENTALS rental : rentalsList) {
            RentalsDataTransferObject rentalsDto = new RentalsDataTransferObject();
            Integer rentalOwner = rental.getOwnerId();
            System.out.println("Rental owner ID: " + rentalOwner);
            
            rentalsDto.setId(rental.getId());
            rentalsDto.setName(rental.getName());
            rentalsDto.setSurface(rental.getSurface());
            rentalsDto.setPrice(rental.getPrice());
            rentalsDto.setPictureUrl(rental.getPicture());
            rentalsDto.setDescription(rental.getDescription());
            rentalsDto.setOwnerId(rental.getOwnerId());
            rentalsDto.setCreated_at(rental.getCreatedAt());
            rentalsDto.setUpdated_at(rental.getUpdatedAt());

            Map<String, Object> rentalMap = new HashMap<>();
            rentalMap.put("id", rentalsDto.getId());
            rentalMap.put("name", rentalsDto.getName());
            rentalMap.put("surface", rentalsDto.getSurface());
            rentalMap.put("price", rentalsDto.getPrice());
            rentalMap.put("picture", rentalsDto.getPictureUrl());
            rentalMap.put("description", rentalsDto.getDescription());
            if (authenticatedUserId.equals(rentalOwner)) {
                rentalMap.put("ownerId", rentalsDto.getOwnerId());
            } else {
                rentalMap.put("owner_id", rentalsDto.getOwnerId());
            }
            rentalMap.put("ownerId", rentalsDto.getOwnerId());
            rentalMap.put("created_at", rentalsDto.getCreated_at());
            rentalMap.put("updated_at", rentalsDto.getUpdated_at());

            rentalsDtoList.add(rentalMap);
        }
        Map<String, List<Map<String, Object>>> response = new HashMap<>();
        response.put("rentals", rentalsDtoList);
        return ResponseEntity.ok(response);
    }

    /* @GetMapping("/api/rentals/{id}")
    public ResponseEntity<ObjectNode> getRental(@PathVariable Integer id) {
        System.out.println("------------Rental GET id: " + id);
        RENTALS rental = dbRentalsRepository.findById(id).get();

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode rentalsDto = mapper.createObjectNode();

        rentalsDto.put("id", rental.getId());
        rentalsDto.put("name", rental.getName());
        rentalsDto.put("surface", rental.getSurface());
        rentalsDto.put("price", rental.getPrice());
        rentalsDto.put("picture", rental.getPicture());
        rentalsDto.put("description", rental.getDescription());
        rentalsDto.put("owner_id", rental.getOwnerId());
        rentalsDto.put("created_at", rental.getCreatedAt().toString());
        rentalsDto.put("updated_at", rental.getUpdatedAt().toString());

        System.out.println("Returning rental..." + rentalsDto);

        return ResponseEntity.ok(rentalsDto);
    } */
    @GetMapping("/api/rentals/{id}")
        public ResponseEntity<Map<String, Object>> getRental(@PathVariable Integer id){
        RENTALS rental = dbRentalsRepository.findById(id).get();
    
        RentalsDataTransferObject rentalsDto = new RentalsDataTransferObject();
        rentalsDto.setId(rental.getId());
        rentalsDto.setName(rental.getName());
        rentalsDto.setSurface(rental.getSurface());
        rentalsDto.setPrice(rental.getPrice());
        rentalsDto.setPictureUrl(rental.getPicture());
        rentalsDto.setDescription(rental.getDescription());
        rentalsDto.setOwnerId(rental.getOwnerId());
        rentalsDto.setCreated_at(rental.getCreatedAt());
        rentalsDto.setUpdated_at(rental.getUpdatedAt());
    
        Map<String, Object> response = new HashMap<>();
        response.put("id", rentalsDto.getId());
        response.put("name", rentalsDto.getName());
        response.put("surface", rentalsDto.getSurface());
        response.put("price", rentalsDto.getPrice());
        response.put("picture", rentalsDto.getPictureUrl());
        response.put("description", rentalsDto.getDescription());
        response.put("ownerId", rentalsDto.getOwnerId());
        response.put("createdAt", rentalsDto.getCreated_at());
        response.put("updatedAt", rentalsDto.getUpdated_at());

        System.out.println("Returning rental..." + response);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/api/rentals/{id}")
    public ResponseEntity<?> updateRental(@PathVariable Integer id, @ModelAttribute RentalsDataTransferObject rentalsDto) {
        System.out.println("------------Rental PUT id: " + rentalsDto.getId());
        RENTALS rental = dbRentalsRepository.findById(id).get();
        
        rental.setName(rentalsDto.getName());
        rental.setSurface(rentalsDto.getSurface());
        rental.setPrice(rentalsDto.getPrice());
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
