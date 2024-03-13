package com.ocp3.rental.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.DTO.RentalsDataTransferObject;
import com.ocp3.rental.model.RentalsEntity;
import com.ocp3.rental.repository.DBRentalsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
public class DetailRentalController {

    @Autowired
    private DBRentalsRepository dbRentalsRepository;

    @GetMapping("/api/rentals/{id}")
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
        public ResponseEntity<Map<String, Object>> getRental(@PathVariable Integer id){
        RentalsEntity rental = dbRentalsRepository.findById(id).get();
    
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

}
