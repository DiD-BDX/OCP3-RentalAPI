package com.ocp3.rental.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.DTO.RentalsDataTransferObject;
import com.ocp3.rental.model.RentalsEntity;
import com.ocp3.rental.repository.DBRentalsRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
public class UpdateRentalController {

    @Autowired
    private DBRentalsRepository dbRentalsRepository;
   
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
        
        RentalsEntity rental = dbRentalsRepository.findById(id).get();
        
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
