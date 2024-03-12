package com.ocp3.rental.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ocp3.rental.model.MESSAGES;

public interface DBMessagesRepository extends JpaRepository<MESSAGES, Integer> {
    public Optional<MESSAGES> findById(Integer id);
    public List<MESSAGES> findByMessage(String message);
    public List<MESSAGES> findByRentalId(Integer rentalId);
    public List<MESSAGES> findByUserId(Integer userId);
    public List<MESSAGES> findByCreatedAt(LocalDateTime createdAt);
    public List<MESSAGES> findByUpdatedAt(LocalDateTime updatedAt);
}
