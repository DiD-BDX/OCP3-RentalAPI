package com.ocp3.rental.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.DTO.MessagesDataTransferObject;
import com.ocp3.rental.model.MESSAGES;
import com.ocp3.rental.model.RENTALS;
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

    @Autowired
    private DBMessagesRepository dbMessagesRepository;

    @Autowired
    private DBRentalsRepository dbRentalsRepository;
    
    @Operation(summary = "Send a message to the owner of a rental", security = {
        @SecurityRequirement(name = "bearerToken")})
    
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Message send with success",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{\"message\":\"Message send with success\"}")
        })),
        @ApiResponse(responseCode = "400", description = "bad Request",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "{}")
        })),
        @ApiResponse(responseCode = "401", description = "Unauthorized",
                    content = @Content(mediaType = "application/json",
                    examples = { @ExampleObject(value = "")
        }))
    })
    @PostMapping("/api/messages**")
    public ResponseEntity<?> messages(HttpServletRequest request, @RequestBody MessagesDataTransferObject messages) {
        System.out.println("Starting register method...");

        // Vérifie si le header Authorization est différent de Bearer jwt
        String authorizationHeader = request.getHeader("Authorization");
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
        return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        System.out.println(messages);

        // Récupere le ownerID à partir de rental_id
        Integer messagesRentalId = messages.getRental_id();
        RENTALS rental = dbRentalsRepository.findById(messagesRentalId).get();
        Integer owner_id = rental.getOwnerId();
        messages.setUser_id(owner_id);
        System.out.println("ID message: " + messages.getUser_id());

        if (messages.getUser_id() == null ||
            messages.getMessage() == null ||
            messages.getRental_id() == null) {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            return new ResponseEntity<>("{}", headers, HttpStatus.BAD_REQUEST);
        }
       
        var messagesEntity = new MESSAGES();
        messagesEntity.setUserId(messages.getUser_id());
        messagesEntity.setMessage(messages.getMessage());
        messagesEntity.setRentalId(messages.getRental_id());

        // Enregistre le message dans la base de données
        System.out.println("Saving message...");
        dbMessagesRepository.save(messagesEntity);
        System.out.println("Message saved");

        // Crée une map avec le message de succès
        Map<String, String> response = new HashMap<>();
        response.put("message", "Message send with success");

        System.out.println("Returning response...");
        return ResponseEntity.ok(response);
    }

    
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleInvalidJson(HttpMessageNotReadableException ex) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    return new ResponseEntity<>("{}", headers, HttpStatus.BAD_REQUEST);
    }
  

}
