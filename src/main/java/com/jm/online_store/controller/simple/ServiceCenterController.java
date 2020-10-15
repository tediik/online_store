package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Контроллер сервисного центра
 */
@Controller
public class ServiceCenterController {

    private final UserService userService;

    @Autowired
    public ServiceCenterController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/serviceCenter")
    public String getServiceCenter(){
        return "serviceCenter";
    }

    @GetMapping("/serviceCheckStatus")
    public String getServiceCheckStatus(){
        return "serviceCheckStatus";
    }

    @GetMapping("/service")
    public String getServiceProfile(Model model) {
        User user = userService.getCurrentLoggedInUser();
        model.addAttribute("user", user);
        return "servicePage";
    }

    @PostMapping("/service/profile")
    public String updateServiceProfile(User user, Model model) {
        User updateUser = userService.updateUserProfile(user);
        model.addAttribute("user", updateUser);
        return "servicePage";
    }
}
