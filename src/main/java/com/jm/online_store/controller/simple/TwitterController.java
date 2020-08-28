package com.jm.online_store.controller.simple;

import com.jm.online_store.config.security.odnoklassniki.OAuth2Odnoklassniki;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


@AllArgsConstructor
@Controller
@RequestMapping("/")
public class TwitterController {

    private final OAuth2Odnoklassniki oAuth2Odnoklassniki;

    @PostMapping("/oauth")
    public String oAuthOdnoklassniki(@RequestParam String code) {
        oAuth2Odnoklassniki.UserAuth(code);
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginPage(Model model) {
        String authUrlOK = oAuth2Odnoklassniki.getAuthorizationUrl();
        model.addAttribute("authUrlOK", authUrlOK);
        return "login";
    }

    @GetMapping("/denied")
    public String deniedPage() {
        return "denied";
    }
}
