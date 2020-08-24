package com.jm.online_store.controller.simple;

import com.jm.online_store.config.security.odnoklassniki.OAuth2Service;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class LoginController {

    private final OAuth2Service oAuth2Service;

    @GetMapping("/oauth")
    public String oAuthOdnoklassniki(Model model, @RequestParam String code) {

        oAuth2Service.UserAuth(code);
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginPage(Model model) {

        String authUrlOK = oAuth2Service.getAuthorizationUrl();

        model.addAttribute("authUrlOK", authUrlOK);
        return "login";
    }

    @GetMapping("/denied")
    public String deniedPage(){
        return "denied";
    }
}
