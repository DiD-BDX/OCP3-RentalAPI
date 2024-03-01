package com.ocp3.rental.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ocp3.rental.DTO.MessagesDataTransferObject;
import com.ocp3.rental.model.MESSAGES;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBMessagesRepository;
import com.ocp3.rental.service.JWTService;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api/messages/")
public class MessagesController {

    @Autowired
    private DBMessagesRepository dbMessagesRepository;

    @Autowired
    private JWTService jwtService;

    @PostMapping
    public ResponseEntity<?> messages(HttpServletRequest request, @RequestBody MessagesDataTransferObject messages) {
    //public ResponseEntity<?> messages(@RequestBody MessagesDataTransferObject messages) {
        System.out.println("Starting register method...");

        System.out.println("Message: " + messages.getMessage());

     // Récupère l'utilisateur authentifié à partir de la requête
        USERS users = jwtService.getUserFromRequest(request);
        String authenticatedUser = users.getName();
        System.out.println("Authenticated user: " + authenticatedUser);
        Integer authenticatedUserId = users.getId();
        System.out.println("ID authenticated: " + authenticatedUserId);
        System.out.println("ID message: " + messages.getUser_id());

        // Vérifie si l'user_id du message correspond à l'ID de l'utilisateur authentifié
        if (!authenticatedUserId.equals(messages.getUser_id())) {
            System.out.println("User ID does not match the authenticated user");
            return new ResponseEntity<>("User ID does not match the authenticated user", HttpStatus.FORBIDDEN);
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

  

}
