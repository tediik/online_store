package com.jm.online_store.controller.rest;

import com.jm.online_store.exception.EmailAlreadyExistsException;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/authority")
@RequiredArgsConstructor
public class AuthorityRestController {
    /**
     * REST-контролллер для ролей ADMIN & MANAGER
     */
    private final UserService userService;

    @PostMapping("/changemail")
    public ResponseEntity<String> changeMailReq(Authentication auth, Model model,
                                                @RequestParam String newMail) {
        User user = (User) auth.getPrincipal();
        if (userService.isExist(newMail)) {
            throw new EmailAlreadyExistsException();
        }
        userService.changeUsersMail(user, newMail);
        return ResponseEntity.ok("success");
    }
}
