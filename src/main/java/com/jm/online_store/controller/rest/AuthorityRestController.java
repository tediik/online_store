package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/authority")
@RequiredArgsConstructor
@Api(description = "Rest controller for actions with profiles with roles: Admin, Manager, Service")
public class AuthorityRestController {
    /**
     * REST-контролллер для ролей ADMIN & MANAGER & SERVICE
     */
    private final UserService userService;

    @PostMapping("/changemail")
    public ResponseEntity<String> changeMailReq(@RequestParam String newMail) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.isExist(newMail)) {
            throw new EmailAlreadyExistsException();
        }
        userService.changeUsersMail(user, newMail);
        return ResponseEntity.ok("success");
    }

    /**
     * Метод изменения email
     * @param newMail принимает новый email
     * @return ResponseEntity<String> возвращает статус ответа
     */
    @PostMapping("/changeEmail")
    @ApiOperation(value = "change email")
    public ResponseEntity<String> changeMail(@RequestBody String newMail) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.isExist(newMail)) {
            return ResponseEntity.badRequest().build();
        } else {
            userService.changeUsersMail(user, newMail);
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Метод изменения пароля
     * @param passwords принимает старый и новый пароли в виде карты
     * @return ResponseEntity<String> возвращает статус ответа
     */
    @PostMapping("/changePassword")
    @ApiOperation(value = "change password")
    public ResponseEntity<String> changePassword(@RequestBody Map<String, String> passwords) {
        User user = userService.getCurrentLoggedInUser();
        if (!userService.changePassword(user.getId(), passwords.get("oldPassword"), passwords.get("newPassword"))) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Метод получения текущего пользователя
     * @return ResponseEntity<User> возвращает текущего пользователя и статус ответа
     */
    @ApiOperation(value = "get current Logged in User")
    @GetMapping(value = "/currentUser")
    public ResponseEntity<User> getCurrentUser() {
        User currentLoggedInUser = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(currentLoggedInUser);
    }

    /**
     * Метод обновления профиля
     * @param user текущий пользователь
     * @return ResponseEntity<String> статус ответа
     */
    @PutMapping("/updateProfile")
    @ApiOperation(value = "updates current User's profile")
    public ResponseEntity<String> updateProfile(@RequestBody User user) {
        userService.updateUserProfile(user);
        return ResponseEntity.ok().build();
    }
    /**
     * Метод удаления профиля
     * @param id индентификатор пользователя
     * @return ResponseEntity.ok() код ответа
     */
    @DeleteMapping("/deleteProfile/{id}")
    @ApiOperation(value = "deletes current User's profile")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        userService.deleteByID(id);
        return ResponseEntity.ok().build();
    }
}