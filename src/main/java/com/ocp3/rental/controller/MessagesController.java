package com.ocp3.rental.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.DTO.MessagesDataTransferObject;
import com.ocp3.rental.model.MessagesEntity;
import com.ocp3.rental.model.RentalsEntity;
import com.ocp3.rental.repository.DBMessagesRepository;
import com.ocp3.rental.repository.DBRentalsRepository;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
public class MessagesController {

    // Injection des dépendances pour les repositories
    @Autowired
    private DBMessagesRepository dbMessagesRepository;
    @Autowired
    private DBRentalsRepository dbRentalsRepository;
    
    // Définition de l'opération d'envoi de message avec les réponses API possibles
    @Operation(summary = "Send a message to the owner of a rental", security = {@SecurityRequirement(name = "bearerToken")})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message send with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\"message\":\"Message send with success\"}")})),
        @ApiResponse(responseCode = "400", description = "bad Request",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{}")})),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "")}))
    })
    @PostMapping("/api/messages**")
    public ResponseEntity<?> messages(HttpServletRequest request, @RequestBody MessagesDataTransferObject messages) {

        // Vérification de l'autorisation
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        // Récupération de l'ID de location et vérification de son existence
        Integer messagesRentalId = messages.getRental_id();
        Optional<RentalsEntity> rentalOpt = dbRentalsRepository.findById(messagesRentalId);
        if (!rentalOpt.isPresent()) {
            return ResponseEntity.badRequest().body("{}");
        }
        // Récupération du propriétaire de la location
        RentalsEntity rental = rentalOpt.get();
        Integer owner_id = rental.getOwnerId();
        messages.setUser_id(owner_id);

        // Vérification de la validité des données du message
        if (messages.getUser_id() == null || messages.getMessage() == null || messages.getRental_id() == null) {
            return ResponseEntity.badRequest().body("{}");
        }
       
        // Création et sauvegarde du message
        var messagesEntity = new MessagesEntity();
        messagesEntity.setUserId(messages.getUser_id());
        messagesEntity.setMessage(messages.getMessage());
        messagesEntity.setRentalId(messages.getRental_id());
        dbMessagesRepository.save(messagesEntity);

        // Création de la réponse
        Map<String, String> response = new HashMap<>();
        response.put("message", "Message send with success");
        return ResponseEntity.ok(response);
    }

    // Gestion des exceptions de type HttpMessageNotReadableException
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body("{}");
    }
}