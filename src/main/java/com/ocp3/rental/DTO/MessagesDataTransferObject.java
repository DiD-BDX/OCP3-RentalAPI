package com.ocp3.rental.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessagesDataTransferObject {
    private String message;
    private Integer user_id;
    private Integer rental_id;
}
