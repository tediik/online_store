package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping(value = "/users")
@RequiredArgsConstructor
public class AllUsersController {

    private final UserService userService;

    @PostMapping("/uploadImage")
    @ResponseBody
    public String handleImagePost(@RequestParam("imageFile") MultipartFile imageFile) throws IOException {
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.updateUserImage(userDetails.getId(), imageFile);
    }

    @DeleteMapping("/deleteImage")
    @ResponseBody
    public String deleteImage() throws IOException {
        User userDetails = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return userService.deleteUserImage(userDetails.getId());
    }
}
