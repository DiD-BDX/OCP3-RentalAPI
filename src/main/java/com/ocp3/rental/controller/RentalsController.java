package com.ocp3.rental.controller;

import java.io.IOException;
import java.math.BigDecimal;
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

import com.ocp3.rental.DTO.RentalsDataTransferObject;
import com.ocp3.rental.model.RENTALS;
import com.ocp3.rental.model.USERS;
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

    @Operation(summary = "Create a new rental", security = {
        @SecurityRequirement(name = "bearerToken")})
    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message send with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\"message\":\"Rental created !\"}")
        })),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "")
        }))
    })
    @PostMapping("/api/rentals")
    public ResponseEntity<?> rentals(HttpServletRequest request,
        @ModelAttribute RentalsDataTransferObject rentalsDto,
        @RequestParam("picture") MultipartFile image) throws IOException {

        // Récupère l'utilisateur authentifié à partir de la requête
        USERS users = jwtService.getUserFromRequest(request);
        Integer authenticatedUserId = users.getId();

        // Enregistre l'image et récupère le chemin du fichier
        String pictureName = apiServices.savePictureFromRequest(image, request);
        
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

    @Operation(summary = "List all rentals", security = {
        @SecurityRequirement(name = "bearerToken")})
    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "List of rentals with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{ \n" + //
                                                "  \"rentals\": [\n" + //
                                                "  {\n" + //
                                                "\t\"id\": 1,\n" + //
                                                "\t\"name\": \"test house 1\",\n" + //
                                                "\t\"surface\": 432,\n" + //
                                                "\t\"price\": 300,\n" + //
                                                "\t\"picture\": \"https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg\",\n" + //
                                                "\t\"description\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam a lectus eleifend, varius massa ac, mollis tortor. Quisque ipsum nulla, faucibus ac metus a, eleifend efficitur augue. Integer vel pulvinar ipsum. Praesent mollis neque sed sagittis ultricies. Suspendisse congue ligula at justo molestie, eget cursus nulla tincidunt. Pellentesque elementum rhoncus arcu, viverra gravida turpis mattis in. Maecenas tempor elementum lorem vel ultricies. Nam tempus laoreet eros, et viverra libero tincidunt a. Nunc vel nisi vulputate, sodales massa eu, varius erat.\",\n" + //
                                                "\t\"owner_id\": 1,\n" + //
                                                "\t\"created_at\": \"2012/12/02\",\n" + //
                                                "\t\"updated_at\": \"2014/12/02\"  \n" + //
                                                "},\n" + //
                                                "{\n" + //
                                                "\t\"id\": 1,\n" + //
                                                "\t\"name\": \"test house 2\",\n" + //
                                                "\t\"surface\": 154,\n" + //
                                                "\t\"price\": 200,\n" + //
                                                "\t\"picture\": \"https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg\",\n" + //
                                                "\t\"description\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam a lectus eleifend, varius massa ac, mollis tortor. Quisque ipsum nulla, faucibus ac metus a, eleifend efficitur augue. Integer vel pulvinar ipsum. Praesent mollis neque sed sagittis ultricies. Suspendisse congue ligula at justo molestie, eget cursus nulla tincidunt. Pellentesque elementum rhoncus arcu, viverra gravida turpis mattis in. Maecenas tempor elementum lorem vel ultricies. Nam tempus laoreet eros, et viverra libero tincidunt a. Nunc vel nisi vulputate, sodales massa eu, varius erat.\",\n" + //
                                                "\t\"owner_id\": 2,\n" + //
                                                "\t\"created_at\": \"2012/12/02\",\n" + //
                                                "\t\"updated_at\": \"2014/12/02\"  \n" + //
                                                "},{\n" + //
                                                "\t\"id\": 3,\n" + //
                                                "\t\"name\": \"test house 3\",\n" + //
                                                "\t\"surface\": 234,\n" + //
                                                "\t\"price\": 100,\n" + //
                                                "\t\"picture\": \"https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg\",\n" + //
                                                "\t\"description\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam a lectus eleifend, varius massa ac, mollis tortor. Quisque ipsum nulla, faucibus ac metus a, eleifend efficitur augue. Integer vel pulvinar ipsum. Praesent mollis neque sed sagittis ultricies. Suspendisse congue ligula at justo molestie, eget cursus nulla tincidunt. Pellentesque elementum rhoncus arcu, viverra gravida turpis mattis in. Maecenas tempor elementum lorem vel ultricies. Nam tempus laoreet eros, et viverra libero tincidunt a. Nunc vel nisi vulputate, sodales massa eu, varius erat.\",\n" + //
                                                "\t\"owner_id\": 1,\n" + //
                                                "\t\"created_at\": \"2012/12/02\",\n" + //
                                                "\t\"updated_at\": \"2014/12/02\"  \n" + //
                                                "}\n" + //
                                                "  \n" + //
                                                "  ]\n" + //
                                                "}")
        })),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "")
        }))
    })
    @GetMapping("/api/rentals")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getRentals(HttpServletRequest request) {
        List<RENTALS> rentalsList = dbRentalsRepository.findAll();

        // Recupère les valeurs de l'utilisateur authentifié
        String token = request.getHeader("Authorization").substring(7);
        Integer authenticatedUserId = jwtService.getUserIdFromToken(token);

        List<Map<String, Object>> rentalsDtoList = new ArrayList<>();

        for (RENTALS rental : rentalsList) {
            RentalsDataTransferObject rentalsDto = new RentalsDataTransferObject();
            Integer rentalOwner = rental.getOwnerId();
            
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

    @Operation(summary = "Details of a rental", security = {
        @SecurityRequirement(name = "bearerToken")})
    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Details of a rental with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\n" + //
                                                "\t\"id\": 1,\n" + //
                                                "\t\"name\": \"dream house\",\n" + //
                                                "\t\"surface\": 24,\n" + //
                                                "\t\"price\": 30,\n" + //
                                                "\t\"picture\": [\"https://blog.technavio.org/wp-content/uploads/2018/12/Online-House-Rental-Sites.jpg\"],\n" + //
                                                "\t\"description\": \"Lorem ipsum dolor sit amet, consectetur adipiscing elit. Etiam a lectus eleifend, varius massa ac, mollis tortor. Quisque ipsum nulla, faucibus ac metus a, eleifend efficitur augue. Integer vel pulvinar ipsum. Praesent mollis neque sed sagittis ultricies. Suspendisse congue ligula at justo molestie, eget cursus nulla tincidunt. Pellentesque elementum rhoncus arcu, viverra gravida turpis mattis in. Maecenas tempor elementum lorem vel ultricies. Nam tempus laoreet eros, et viverra libero tincidunt a. Nunc vel nisi vulputate, sodales massa eu, varius erat.\",\n" + //
                                                "\t\"owner_id\": 1,\n" + //
                                                "\t\"created_at\": \"2012/12/02\",\n" + //
                                                "\t\"updated_at\": \"2014/12/02\"  \n" + //
                                                "}")
        })),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "")
        }))
    })
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
        response.put("created_at", rentalsDto.getCreated_at());
        response.put("updated_at", rentalsDto.getUpdated_at());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Edit a rental", security = {
        @SecurityRequirement(name = "bearerToken")})
    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Rental updated with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\"message\":\"Rental updated !\"}")
        })),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "")
        }))
    })
    @PutMapping("/api/rentals/{id}")
    public ResponseEntity<?> updateRental(
    @PathVariable Integer id, 
    @Parameter(description = "Rental's name", example = "My Rental", in = ParameterIn.QUERY) @RequestParam String name,
    @Parameter(description = "Rental's surface", example = "100", in = ParameterIn.QUERY) @RequestParam BigDecimal surface,
    @Parameter(description = "Rental's price", example = "1000", in = ParameterIn.QUERY) @RequestParam BigDecimal price,
    @Parameter(description = "Rental's description", example = "A beautiful rental", in = ParameterIn.QUERY) @RequestParam String description
) {
    RentalsDataTransferObject rentalsDto = new RentalsDataTransferObject();
    rentalsDto.setName(name);
    rentalsDto.setSurface(surface);
    rentalsDto.setPrice(price);
    rentalsDto.setDescription(description);   
        
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
