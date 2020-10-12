package com.jm.online_store.controller.rest;

import com.jm.online_store.model.User;
import com.jm.online_store.model.dto.UserDto;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class AllUsersRestController {

    private final UserService userService;

    /**
     * Метод для получения имейла и ролей залогиненного пользователя,
     * в дальнейшем используемых на фронте при отдаче вариации страницы профиля.
     * @param authentication
     * @return
     */
    @GetMapping("/getCurrent")
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

    @PostMapping("/uploadImage")
    public ResponseEntity<String> handleImagePost(@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        User userDetails = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(userService.updateUserImage(userDetails.getId(), imageFile));
    }

    @DeleteMapping("/deleteImage")
    public ResponseEntity<String> deleteImage() throws IOException {
        User userDetails = userService.getCurrentLoggedInUser();
        return ResponseEntity.ok(userService.deleteUserImage(userDetails.getId()));
    }
}
