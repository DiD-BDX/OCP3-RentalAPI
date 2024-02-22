package com.ocp3.rental.repository;

import org.springframework.data.jpa.repository.JpaRepository;


import com.ocp3.rental.model.USERS;

public interface DBUserRepository extends JpaRepository<USERS, Integer> {
	public USERS findByEmail(String email);
	public USERS findByName(String name);
	
	
}