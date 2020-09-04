package com.jm.online_store.model.dto;

import com.jm.online_store.model.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

@Data
@Accessors(chain = true)
@NoArgsConstructor
public class UserDto {
    private String email;
    private Set<Role> roles;
}
