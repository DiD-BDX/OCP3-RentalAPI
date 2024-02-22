package com.ocp3.rental.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/auth")
public class ResourceController {

    @GetMapping("/me")
    public String me() {
        return "c'est ok";
    }
}