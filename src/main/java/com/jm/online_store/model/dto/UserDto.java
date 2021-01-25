package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description =  "DTO для данных юзера")
public class UserDto {
    private String email;
    private String password;
    private Set<Role> roles;
    private String firstName;
    Collection<? extends GrantedAuthority> authorities;
    boolean enabled;

    public User toUser(){
        User user = new User();
        user.setEmail(email);
        user.setFirstName(firstName);
        return user;
    }

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        return userDto;
    }
}