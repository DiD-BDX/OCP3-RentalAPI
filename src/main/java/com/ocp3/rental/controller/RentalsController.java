package com.ocp3.rental.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.DTO.RentalsDataTransferObject;
import com.ocp3.rental.model.RentalsEntity;
import com.ocp3.rental.repository.DBRentalsRepository;
import com.ocp3.rental.service.JWTService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class RentalsController {
    @Value("${base.picture.path}")
    private String basePicturePath;
    
    @Autowired
    private DBRentalsRepository dbRentalsRepository;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/api/rentals")
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
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getRentals(HttpServletRequest request) {
        List<RentalsEntity> rentalsList = dbRentalsRepository.findAll();

        // Recupère les valeurs de l'utilisateur authentifié
        String token = request.getHeader("Authorization").substring(7);
        Integer authenticatedUserId = jwtService.getUserIdFromToken(token);

        List<Map<String, Object>> rentalsDtoList = new ArrayList<>();

        for (RentalsEntity rental : rentalsList) {
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
}
