package com.jm.online_store.controller.simple;

import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер сервисного центра
 */
@Controller
@AllArgsConstructor
public class ServiceCenterController {

    private final UserService userService;

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
}