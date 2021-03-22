package com.jm.online_store.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jm.online_store.model.Role;
import com.jm.online_store.model.User;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import java.time.LocalDate;
import java.util.Set;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@ApiModel(description = "DTO для данных юзера")
public class UserDto {
    private Long id;
    private String email;
    private Boolean isAccountNonBlockedStatus;
    private boolean isAccountNonReadOnlyStatus;
    private String firstName;
    private String lastName;
    private String password;
    private String phoneNumber;
    private User.Gender userGender;
    private LocalDate birthdayDate;
    private LocalDate registerDate;
    private String profilePicture;
    private Set<Role> roles;
    private String active;

    public static UserDto fromUser(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getUsername());
        userDto.setFirstName(user.getFirstName());
        userDto.setLastName(user.getLastName());
        userDto.setPhoneNumber(user.getPhoneNumber());
        userDto.setUserGender(user.getUserGender());
        userDto.setBirthdayDate(user.getBirthdayDate());
        userDto.setRegisterDate(user.getRegisterDate());
        userDto.setProfilePicture(user.getProfilePicture());
        userDto.setAccountNonReadOnlyStatus(user.getIsAccountNonBlockedStatus());
        userDto.setRoles(user.getRoles());
        userDto.setPassword(user.getPassword());
        userDto.setAccountNonReadOnlyStatus(user.isAccountNonReadOnlyStatus());
        userDto.setIsAccountNonBlockedStatus(user.getIsAccountNonExpiredStatus());
        return userDto;
    }
}
