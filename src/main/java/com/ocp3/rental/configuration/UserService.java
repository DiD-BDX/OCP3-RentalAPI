package com.ocp3.rental.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.ocp3.rental.model.USERS;
import com.ocp3.rental.repository.DBUserRepository;

import jakarta.transaction.Transactional;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private DBUserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public void encodePasswords() {
        List<USERS> users = userRepository.findAll();
        for (USERS user : users) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
            userRepository.save(user);
        }
    }
}