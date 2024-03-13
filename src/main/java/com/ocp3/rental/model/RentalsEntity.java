package com.ocp3.rental.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Entity(name = "RENTALS")
@Data
public class RentalsEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Integer id;

	@Column(name = "name")
	private String name;

	@Column(name = "surface")
    private BigDecimal surface;

	@Column(name = "price")
    private BigDecimal price;

	@Column(name = "picture")
    private String picture;

	@Column(name = "description")
    private String description;

	@Column(name = "owner_id")
    private Integer ownerId;
	
	@Column(name = "created_at")
	@JsonProperty("created_at")
	private LocalDate createdAt;

	@Column(name = "updated_at")
	@JsonProperty("updated_at")
	private LocalDate updatedAt;
}
