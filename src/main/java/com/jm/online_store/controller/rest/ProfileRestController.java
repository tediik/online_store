package com.jm.online_store.controller.rest;

import com.jm.online_store.enums.Response;
import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.PasswordDto;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@Api(description = "Rest controller for actions with profiles with roles: Admin, Manager, Service")
public class ProfileRestController {
    /**
     * REST-контролллер для ролей ADMIN & MANAGER & SERVICE
     */
    private final UserService userService;
    private final ModelMapper modelMapper;


    /**
     * Метод изменения email
     * @param newMail принимает новый email
     * @return ResponseEntity<>(body, HttpStatus)
     */
    @PostMapping("/changeEmail")
    @ApiOperation(value = "change email",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<String>> changeMail(@RequestBody String newMail) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.isExist(newMail)) {
            return new ResponseEntity<>(new ResponseDto<>(false, "Пользователь с таким email уже существует"), HttpStatus.BAD_REQUEST);
        } else {
            userService.changeUsersMail(user, newMail);
            return new ResponseEntity<>(new ResponseDto<>(true, "На почту " + newMail + " было отправлено сообщение с подтверждением.", Response.NO_ERROR.getText()), HttpStatus.OK);
        }
    }

    /**
     * Метод изменения пароля пользователя.
     * @param passwords старый и новый пароль из PasswordDto
     * @return ResponseEntity<>(body, HttpStatus)
     */
    @PostMapping("/changePassword")
    @ApiOperation(value = "processes request to change password",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "No user with such id"),
            @ApiResponse(code = 200, message = "Изменения для пользователя с идентификатором: \"id\" были успешно добавлены."),
    })
    public ResponseEntity<ResponseDto<String>> changePassword(@RequestBody PasswordDto passwords) {
        User user = userService.getCurrentLoggedInUser();
        if (passwords.getNewPassword().equals(passwords.getOldPassword())) {
            return new ResponseEntity<>(new ResponseDto<>(false, "Старый и новый пароли совпадают."), HttpStatus.BAD_REQUEST);
        }
        if (!userService.changePassword(user.getId(), passwords.getOldPassword(), passwords.getNewPassword())) {
            return new ResponseEntity<>(new ResponseDto<>(false, "Ошибка при изменении пароля."), HttpStatus.BAD_REQUEST);
        }
        log.info("Пароль для пользователя: {} успешно изменён.", user.getEmail());
        return new ResponseEntity<>(new ResponseDto<>(true, "Изменения для пользователя с идентификатором: " + user.getId() + " были успешно добавлены. ", Response.NO_ERROR.getText()), HttpStatus.OK);
    }

    /**
     * Метод получения текущего пользователя
     * @return ResponseEntity<User> возвращает текущего пользователя и статус ответа
     */
    @GetMapping(value = "/currentUser")
    @ApiOperation(value = "get current Logged in User",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Пользователь не найден"),
            @ApiResponse(code = 200, message = "Пользователь с идентификатором: \"id\" найден."),
    })
    public ResponseEntity<ResponseDto<UserDto>> getCurrentUser() {
        User currentLoggedInUser = userService.getCurrentLoggedInUser();
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(currentLoggedInUser, UserDto.class)), HttpStatus.OK);
    }

    /**
     * Метод обновления профиля
     * @param userDto {@link UserDto}текущий пользователь
     * @return new ResponseEntity<ResponseDto<UserDto>>(ResponseDto, HttpStatus) {@link ResponseEntity}
     */
    @PutMapping("/update")
    @ApiOperation(value = "updates current User's profile",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<UserDto>> updateProfile(@RequestBody UserDto userDto) {
        userService.updateUserDtoProfile(userDto);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(userDto, UserDto.class)), HttpStatus.OK);
    }

    /**
     * Метод удаления профиля
     * @param id индентификатор пользователя
     * @return ResponseEntity<ResponseDto<UserDto>>(user, HttpStatus) {@link ResponseEntity}
     */
    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "deletes current User's profile",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<ResponseDto<UserDto>> deleteProfile(@PathVariable Long id) {
        userService.deleteByID(id);
        return new ResponseEntity<>(new ResponseDto<>(true, modelMapper.map(userService.findUserById(id), UserDto.class)), HttpStatus.OK);
    }
}
