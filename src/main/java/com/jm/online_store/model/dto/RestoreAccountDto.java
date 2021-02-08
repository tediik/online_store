package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jm.online_store.model.User;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "DTO для восстановления аккаунта юзера")
public class RestoreAccountDto {
    private String email;
    private String password;

    public static RestoreAccountDto fromUser(User user) {
        RestoreAccountDto restoreAccountDto = new RestoreAccountDto();
        restoreAccountDto.setEmail(user.getUsername());
        restoreAccountDto.setPassword(user.getPassword());
        return restoreAccountDto;
    }

}
