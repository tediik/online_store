package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @PreAuthorize("permitAll()")
    @PostMapping("/registration")
    @ApiOperation(value = "Registration of the new account")
    @ApiResponse(code = 200, message = "This response can also display error, see the comment inside it. Only \"success\" comment means that account was registered. Other options:" +
            " BindingResult in registerUserAccount hasErrors: ..., " +
            " Passwords do not match : passwordConfirmError," +
            " Passwords do not match : passwordValidError, " +
            " User with same email already exists," +
            " Wrong email! Не правильно введен email")
    public ResponseEntity registerUserAccount(@ModelAttribute("userForm") @Validated User userForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            log.debug("BindingResult in registerUserAccount hasErrors: {}", bindingResult);
            return new ResponseEntity("Binding error", HttpStatus.OK);
        }
        if (!userForm.getPassword().equals(userForm.getPasswordConfirm())) {
            log.debug("Passwords do not match : passwordConfirmError");
            return ResponseEntity.ok("passwordError");
        }
        if (!ValidationUtils.isValidPassword(userForm.getPassword())) {
            log.debug("Passwords do not match : passwordValidError");
            return ResponseEntity.ok("passwordValidError");
        }
        if (userService.isExist(userForm.getEmail())) {
            log.debug("User with same email already exists");
            return new ResponseEntity("duplicatedEmailError", HttpStatus.OK);
        }
        if (!ValidationUtils.isValidEmail(userForm.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return ResponseEntity.ok("notValidEmailError");
        }
        userService.regNewAccount(userForm);
        return new ResponseEntity("success", HttpStatus.OK);
    }

}