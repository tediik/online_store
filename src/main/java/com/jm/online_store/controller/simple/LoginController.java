package com.jm.online_store.controller.simple;


import com.jm.online_store.model.Role;
import com.jm.online_store.service.RoleService;
import com.jm.online_store.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/")
public class LoginController {



    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;



    @GetMapping(value = "/login")
    public String loginPage() {
        return "login";
    }


    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String userPage() {
        return "customerPage";
    }

    @GetMapping("/denied")
    public String deniedPage(){
        return "denied";
    }



}
