package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Рест контроллер сервисного центра
 */
@RestController
@AllArgsConstructor
public class ServiceRestController {

    private final UserService userService;

    /**
     * Метод изменения email работника сервисного центра
     * @param newMail принимает новый email
     * @return ResponseEntity<String> возвращает статус ответа
     */
    @PostMapping("/service/changeEmail")
    public ResponseEntity<String> changeMailServiceWorker(@RequestBody String newMail) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.isExist(newMail)) {
            return ResponseEntity.badRequest().build();
        } else {
            userService.changeUsersMail(user, newMail);
            return ResponseEntity.ok().build();
        }
    }

    /**
     * Метод изменения пароля работника сервисного центра
     * @param passwords принимает старый и новый пароли в виде карты
     * @return ResponseEntity<String> возвращает статус ответа
     */
    @PostMapping("/service/changePassword")
    public ResponseEntity<String> changePasswordServiceWorker(@RequestBody Map<String, String> passwords) {
        User user = userService.getCurrentLoggedInUser();
        if (!userService.changePassword(user.getId(), passwords.get("oldPassword"), passwords.get("newPassword"))) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * Метод удаления профиля работника сервиса
     * @param id индентификатор работника сервиса
     * @return ResponseEntity.ok() код ответа
     */
    @DeleteMapping("/service/deleteProfile/{id}")
    public ResponseEntity<String> deleteProfileServiceWorker(@PathVariable Long id) {
        userService.deleteByID(id);
        return ResponseEntity.ok().build();
    }
}