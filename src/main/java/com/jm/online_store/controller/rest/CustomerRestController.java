package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            return ResponseEntity.badRequest().body("Ой! Вы ввели такой же Email.");
        } else {
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
    @PostMapping("/deleteProfile/{id}")
    public ResponseEntity<String> deleteProfile(@PathVariable Long id) {
        userService.changeUserStatus(id);
        return ResponseEntity.ok("Delete profile");
    }
}
