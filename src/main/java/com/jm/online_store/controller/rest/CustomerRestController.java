package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import com.jm.online_store.util.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/customer")
@Slf4j
public class CustomerRestController {

    @Autowired
    private UserService userService;

    @PostMapping("/changemail")
    public ResponseEntity<String> changeMailReq(@RequestParam String newMail) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.isExist(newMail)) {
            log.debug("Вы ввели такой же Email.");
            return new ResponseEntity("duplicatedEmailError", HttpStatus.BAD_REQUEST);
        }
        if (ValidationUtils.isNotValidEmail(newMail)) {
            return new ResponseEntity("notValidEmailError", HttpStatus.BAD_REQUEST);
        } else {
            userService.changeUsersMail(user, newMail);
            return ResponseEntity.ok("Email будет изменен после подтверждения.");
        }
    }

    /**
     * метод обработки изменения пароля User.
     *
     * @param model       модель для view
     * @param oldPassword старый пароль
     * @param newPassword новый пароль
     * @return страница User
     */
    @PostMapping("/change-password")
    public ResponseEntity changePassword(Model model,
                                         @RequestParam String oldPassword,
                                         @RequestParam String newPassword) {
        User user = userService.getCurrentLoggedInUser();
        if (userService.findById(user.getId()).isEmpty()) {
            log.debug("Нет пользователя с идентификатором: {}", user.getId());
            return ResponseEntity.noContent().build();
        }
        if (ValidationUtils.isNotValidEmail(user.getEmail())) {
            log.debug("Wrong email! Не правильно введен email");
            return ResponseEntity.badRequest().body("notValidEmailError");
        }
        if (!userService.findById(user.getId()).get().getEmail().equals(user.getEmail())
                && userService.isExist(user.getEmail())) {
            log.debug("Пользователь с таким адресом электронной почты уже существует");
            return ResponseEntity.badRequest().body("duplicatedEmailError");
        }
        if (userService.changePassword(user.getId(), oldPassword, newPassword))
            log.debug("Изменения для пользователя с идентификатором: {} был успешно добавлен", user.getId());
        return ResponseEntity.ok().build();
    }

    /**
     * Метод удаления профиля покупателя
     *
     * @param id идентификатор покупателя
     * @return ResponseEntity.ok()
     */
    @DeleteMapping("/deleteProfile/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        userService.deleteByID(id);
        return ResponseEntity.ok("Delete profile");
    }
}
