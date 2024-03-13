package com.ocp3.rental.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocp3.rental.model.MessagesEntity;

public interface DBMessagesRepository extends JpaRepository<MessagesEntity, Integer> {
    public Optional<MessagesEntity> findById(Integer id);
    public List<MessagesEntity> findByMessage(String message);
    public List<MessagesEntity> findByRentalId(Integer rentalId);
    public List<MessagesEntity> findByUserId(Integer userId);
    public List<MessagesEntity> findByCreatedAt(LocalDateTime createdAt);
    public List<MessagesEntity> findByUpdatedAt(LocalDateTime updatedAt);
}
