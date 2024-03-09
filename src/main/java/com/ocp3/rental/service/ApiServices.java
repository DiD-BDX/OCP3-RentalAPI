package com.ocp3.rental.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;


@Service
public class ApiServices {

    public String savePictureFromRequest(MultipartFile image, HttpServletRequest request) throws IOException {
        // Vérifiez si le fichier est vide
        if (image.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        // Générer le nom du fichier
        String fileName = StringUtils.cleanPath(Objects.requireNonNull(image.getOriginalFilename()));

        // Créer le chemin du fichier
        Path uploadPath = Paths.get("src/main/resources/static/images/");

        // Créer le répertoire s'il n'existe pas
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Enregistrer le fichier
        Path filePath = uploadPath.resolve(fileName);
        System.out.println("------ File path: " + filePath);
        image.transferTo(filePath);
        

        return fileName.toString();
    }

    

}
