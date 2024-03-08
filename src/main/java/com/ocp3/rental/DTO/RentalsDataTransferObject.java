package com.ocp3.rental.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RentalsDataTransferObject {
    private Integer id;
    private String name;
    private BigDecimal surface;
    private BigDecimal price;
    private MultipartFile picture;
    private String pictureUrl;
    private String description;
    private Integer ownerId;
    private LocalDate created_at;
    private LocalDate updated_at;

}
