package com.jm.online_store.service.builder;

import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDtoBuilder {
    public UserDto build(User user) {
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        userDto.setFirstName(user.getFirstName());
        return userDto;
    }
}
