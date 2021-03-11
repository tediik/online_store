package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.ResponseDto;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Рест контроллер для регистрации нового пользователя.
 */
@RestController
@RequestMapping("/api")
@Slf4j
@RequiredArgsConstructor
@Api(description = "Rest controller of the registration")
public class RegistrationRestController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @PreAuthorize("permitAll()")
    @PostMapping("/registration")
    @ApiOperation(value = "Registration of the new account")
    @ApiResponse(code = 200, message = "This response can also display error, see the comment inside it. Only \"success\" comment means that account was registered. Other options:" +
            " BindingResult in registerUserAccount hasErrors: ..., " +
            " Passwords do not match : passwordConfirmError," +
            " Passwords do not match : passwordValidError, " +
            " User with same email already exists," +
            " Wrong email! Не правильно введен email")
    public ResponseEntity<ResponseDto<?>> registerUserAccount(@ModelAttribute("userForm") @Validated User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.debug("BindingResult in registerUserAccount hasErrors: {}", bindingResult);
            return new ResponseEntity(new ResponseDto<>(false, "Binding error"), HttpStatus.BAD_REQUEST);
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            log.debug("Passwords do not match : passwordConfirmError");
            return new ResponseEntity(new ResponseDto<>(false, "passwordError"), HttpStatus.BAD_REQUEST);
        }
        if (!ValidationUtils.isValidPassword(userForm.getPassword())) {
            log.debug("Passwords do not match : passwordValidError");
            return new ResponseEntity(new ResponseDto<>(false, "passwordValidError"), HttpStatus.BAD_REQUEST);
        }
        if (userService.isExist(userForm.getEmail())) {
            log.debug("User with same email already exists");
            return new ResponseEntity(new ResponseDto<>(false, "duplicatedEmailError"), HttpStatus.BAD_REQUEST);
        }
        if (!ValidationUtils.isValidEmail(userForm.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return new ResponseEntity(new ResponseDto<>(false, "notValidEmailError"), HttpStatus.BAD_REQUEST);
        }
        UserDto returnValue = modelMapper.map(userService.regNewAccount(userForm), UserDto.class);
        return ResponseEntity.ok(new ResponseDto<>(true, returnValue));
    }
}