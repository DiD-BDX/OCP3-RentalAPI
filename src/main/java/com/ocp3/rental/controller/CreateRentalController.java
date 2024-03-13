package com.ocp3.rental.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.ocp3.rental.DTO.RentalsDataTransferObject;
import com.ocp3.rental.model.RentalsEntity;
import com.ocp3.rental.model.UsersEntity;
import com.ocp3.rental.repository.DBRentalsRepository;
import com.ocp3.rental.service.ApiServices;
import com.ocp3.rental.service.JWTService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RestController
public class CreateRentalController {
    @Value("${base.picture.path}")
    private String basePicturePath;
    
    @Autowired
    private DBRentalsRepository dbRentalsRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApiServices apiServices;

    @PostMapping("/api/rentals")
    @Operation(summary = "Create a rental", security = {@SecurityRequirement(name = "bearerToken")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rental created with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\"message\":\"Rental created !\"}")})),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "")}))
    })
    public ResponseEntity<?> rentals(HttpServletRequest request,
        @Parameter(description = "Rental's name", example = "My Rental", in = ParameterIn.QUERY) @RequestParam String name,
        @Parameter(description = "Rental's surface", example = "100", in = ParameterIn.QUERY) @RequestParam BigDecimal surface,
        @Parameter(description = "Rental's price", example = "1000", in = ParameterIn.QUERY) @RequestParam BigDecimal price,
        @Parameter(description = "Rental's description", example = "A beautiful rental", in = ParameterIn.QUERY) @RequestParam String description,
        @Parameter(description = "Rental's picture", example = "Select a file", in = ParameterIn.QUERY) @RequestParam MultipartFile picture) throws IOException {

        RentalsDataTransferObject rentalsDto = new RentalsDataTransferObject();
        rentalsDto.setName(name);
        rentalsDto.setSurface(surface);
        rentalsDto.setPrice(price);
        rentalsDto.setDescription(description);
        rentalsDto.setPicture(picture);

        // Récupère l'utilisateur authentifié à partir de la requête
        UsersEntity users = jwtService.getUserFromRequest(request);
        Integer authenticatedUserId = users.getId();

        // Enregistre l'image et récupère le chemin du fichier
        String pictureName = apiServices.savePictureFromRequest(picture, request);
        
        // Crée un nouvel objet RENTALS à partir du DTO
        RentalsEntity rentals = new RentalsEntity();
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

}
