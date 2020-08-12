package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginPage() {
        return "login";
    }


    //Заглушка, заменить на нужную
    @RequestMapping(value = "/someURL", method = RequestMethod.GET)
    public String userPage() {
        return "customerPage";
    }

    @GetMapping("/denied")
    public String deniedPage(){
        return "denied";
    }
}
