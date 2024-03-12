package com.ocp3.rental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocp3.rental.model.RENTALS;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DBRentalsRepository extends JpaRepository<RENTALS, Integer>{
    public Optional<RENTALS> findById(Integer id);
    public RENTALS findByName(String name);
    public RENTALS findBySurface(BigDecimal surface);
    public RENTALS findByPrice(BigDecimal price);
    public RENTALS findByPicture(String picture);
    public RENTALS findByDescription(String description);
    public RENTALS findByOwnerId(Integer ownerId);
    public RENTALS findByCreatedAt(LocalDateTime createdAt);
    public RENTALS findByUpdatedAt(LocalDateTime updatedAt);
    
}
