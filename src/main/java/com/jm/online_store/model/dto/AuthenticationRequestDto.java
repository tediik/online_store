package com.jm.online_store.model.dto;

import lombok.Data;

/**
 * DTO class for authentication (login) request.
 */
@Data
public class AuthenticationRequestDto {
    private String email;
    private String password;
}
