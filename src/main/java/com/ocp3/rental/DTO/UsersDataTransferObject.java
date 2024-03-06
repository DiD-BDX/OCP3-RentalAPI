package com.ocp3.rental.DTO;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsersDataTransferObject {
    private String email;
    private String password;
    private String name;
    private LocalDate created_at;
    private LocalDate updated_at;

}
