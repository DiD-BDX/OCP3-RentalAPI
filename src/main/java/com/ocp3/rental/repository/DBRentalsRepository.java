package com.ocp3.rental.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocp3.rental.model.RentalsEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public interface DBRentalsRepository extends JpaRepository<RentalsEntity, Integer>{
    public Optional<RentalsEntity> findById(Integer id);
    public RentalsEntity findByName(String name);
    public RentalsEntity findBySurface(BigDecimal surface);
    public RentalsEntity findByPrice(BigDecimal price);
    public RentalsEntity findByPicture(String picture);
    public RentalsEntity findByDescription(String description);
    public RentalsEntity findByOwnerId(Integer ownerId);
    public RentalsEntity findByCreatedAt(LocalDateTime createdAt);
    public RentalsEntity findByUpdatedAt(LocalDateTime updatedAt);
    
}
