package com.ocp3.rental.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity
@Data
public class RENTALS {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String name;
    private Number surface;
    private Number price;
    private String picture;
    private String description;
    private long owner_id; 
	private String created_at;
	private String updated_at;
}
