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

/**
 * Рест контроллер сервисного центра
 */
@RestController
@AllArgsConstructor
public class ServiceRestController {

    private final UserService userService;

    @PostMapping("/service/changeEmail")
    public ResponseEntity<String> changeMailReq(@RequestBody String newMail) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.isExist(newMail)) {
            return ResponseEntity.badRequest().body("Ой! Вы ввели такой же Email.");
        } else {
            userService.changeUsersMail(user, newMail);
            return ResponseEntity.ok("Email будет изменен после подтверждения.");
        }
    }

    /**
     * Метод удаления профиля работника сервиса
     *
     * @param id индентификатор работника сервиса
     * @return ResponseEntity.ok() код ответа
     */
    @DeleteMapping("/service/deleteProfile/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        userService.deleteByID(id);
        return ResponseEntity.ok("Delete profile");
    }
}
