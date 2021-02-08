package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.RestoreAccountDto;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.CustomerService;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Authorization;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/allUsers")
@RequiredArgsConstructor
@Api(description = "Rest controller for actions with users profiles")
public class AllUsersRestController {

    private final UserService userService;
    private final CustomerService customerService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Метод для получения имейла и ролей залогиненного пользователя,
     * в дальнейшем используемых на фронте при отдаче вариации страницы профиля.
     *
     * @param authentication
     * @return
     */
    @GetMapping("/getCurrent")
    @ApiOperation(value = "Fetches email and roles of logged in user",
            authorizations = { @Authorization(value = "jwtToken") })
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.noContent().build();
        }
        User currentUser = userService.getCurrentLoggedInUser();
        if (currentUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new UserDto()
                .setEmail(currentUser.getEmail())
                .setRoles(currentUser.getRoles()));
    }

    /**
     * Метод восстановления пользователя
     *
     * @param restoreAccountDto
     * @return
     */
    @PutMapping("/restore")
    @ApiOperation(value = "restores deleted users profile",
            authorizations = { @Authorization(value = "jwtToken") })
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "User not found"),
    })
    public ResponseEntity<String> restoreUser(@RequestBody RestoreAccountDto restoreAccountDto) {
        String msgAlert = "Пользователь не найден!";
        String email = restoreAccountDto.getEmail();
        String passwordOfClient = restoreAccountDto.getPassword();
        String passwordOfBase = userService.getPasswordByMail(email);
        try {
            if (passwordEncoder.matches(passwordOfClient, passwordOfBase)) {
                customerService.restoreCustomer(email);
                msgAlert = "Профиль успешно восстановлен!";
                return ResponseEntity.ok(msgAlert);
            } else {
                throw new UsernameNotFoundException(msgAlert);
            }
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(msgAlert);
        }
    }
}
