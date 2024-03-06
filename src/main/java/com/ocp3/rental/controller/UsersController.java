package com.ocp3.rental.controller;

import org.springframework.web.bind.annotation.RestController;
import com.ocp3.rental.DTO.UsersDataTransferObject;
import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBocp3Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
public class UsersController {

    @Autowired
    private DBocp3Repository dbocp3Repository;

    @GetMapping({"/api/user/{id}", "/api/user/{id}/"})
    public ResponseEntity<UsersDataTransferObject> getUserEntity(@PathVariable Integer id) {
        USERS userData = dbocp3Repository.findById(id).get();
        UsersDataTransferObject userDto = new UsersDataTransferObject();
        userDto.setId(userData.getId());
        userDto.setName(userData.getName());
        userDto.setEmail(userData.getEmail());
        userDto.setCreated_at(userData.getCreatedAt());
        userDto.setUpdated_at(userData.getUpdatedAt());

    return ResponseEntity.ok(userDto);
    }
    

}
