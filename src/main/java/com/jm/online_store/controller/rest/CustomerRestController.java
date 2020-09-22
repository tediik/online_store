package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
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
    public ResponseEntity<String> changeMailReq(Authentication auth, Model model,
                                                @RequestParam String newMail) {
        User user = (User) auth.getPrincipal();
        if (userService.isExist(newMail)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            userService.changeUsersMail(user, newMail);
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }
}
