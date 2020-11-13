package com.jm.online_store.controller.simple;

import com.jm.online_store.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private RoleRepository roleRepository;

    @GetMapping
    public String homePage(Model model) {
        model.addAttribute("listRoles", roleRepository.findAll());
        return "adminPage";
    }
}
