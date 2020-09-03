package com.jm.online_store.controller.simple;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping
    public String getManagerPage() {
        return "managerPage";
    }

    @GetMapping("/news")
    public String getNewsManagementPage() {
        return "newsManagement";
    }
}
