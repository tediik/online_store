package com.jm.online_store.controller.simple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {

    @GetMapping
    public String getManagerPage() {
        return "manager-page";
    }
}
