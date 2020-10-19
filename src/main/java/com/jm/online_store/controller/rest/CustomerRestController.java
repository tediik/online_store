package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.exception.UserNotFoundException;
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
            return ResponseEntity.badRequest().body("duplicatedEmailError");
        }
        if (ValidationUtils.isNotValidEmail(newMail)) {
            return ResponseEntity.badRequest().body("notValidEmailError");
        } else {
            log.debug("вызывает");
            userService.changeUsersMail(user, newMail);

            return ResponseEntity.ok("Email будет изменен после подтверждения.");
        }
    }

    /**
     * Метод удаления профиля покупателя
     *
     * @param id индентификатор покупателя
     * @return ResponseEntity.ok()
     */
    @DeleteMapping("/deleteProfile/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        userService.deleteByID(id);
        return ResponseEntity.ok("Delete profile");
    }
}
