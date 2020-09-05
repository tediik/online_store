package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/users")
public class AllUsersRestController {

    /**
     * Метод для получения имейла и ролей залогиненного пользователя,
     * в дальнейшем используемых на фронте при отдаче вариации страницы профиля.
     * @param authentication
     * @return
     */
    @GetMapping("/getCurrent")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().build();
        }
        User currentUser = (User) authentication.getPrincipal();
        if (currentUser == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new UserDto()
                .setEmail(currentUser.getEmail())
                .setRoles(currentUser.getRoles()));
    }
}
