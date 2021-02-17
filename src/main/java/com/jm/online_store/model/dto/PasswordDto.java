package com.jm.online_store.model.dto;

import lombok.Data;

/**
 * DTO class for change password.
 */
@Data
public class PasswordDto {
    private String oldPassword;
    private String newPassword;
}
