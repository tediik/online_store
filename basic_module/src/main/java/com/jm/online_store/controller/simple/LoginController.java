package com.jm.online_store.controller.simple;

import com.jm.online_store.config.security.Twitter.TwitterAuth;
import com.jm.online_store.config.security.odnoklassniki.OAuth2Odnoklassniki;
import com.jm.online_store.config.security.vk.VkApiClient;
import com.jm.online_store.exception.UserNotFoundException;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@Controller
@RequestMapping("/")
public class LoginController {

    private final OAuth2Odnoklassniki oAuth2Odnoklassniki;
    private final TwitterAuth twitterAuth;
    private final UserService userService;
    private final VkApiClient vkApiClient;

    @GetMapping("/oauth")
    public String oAuthOdnoklassniki(@RequestParam String code) {
        oAuth2Odnoklassniki.UserAuth(code);
        return "redirect:/";
    }

    @GetMapping("/oauthTwitter")
    public String oAuthTwitter(@RequestParam String oauth_verifier) throws InterruptedException, ExecutionException, IOException {
        if (twitterAuth.getAccessToken(oauth_verifier)) {
            return "redirect:/";
        } else {
            return "TwitterRegistrationPage";
        }
    }

    @GetMapping("/TwitterRegistrationPage")
    public String twitterRegPage() {
        return "TwitterRegistrationPage";
    }

    @GetMapping(value = "/login")
    public String loginPage() {
        return "login";
    }

    @RequestMapping(value = "/auth-vk", method = RequestMethod.GET)
    public String loginVk(@RequestParam(value = "code", required = false) String code) {
        vkApiClient.authUser(vkApiClient.requestVkApi(code));
        return "redirect:/";
    }

    @GetMapping("/denied")
    public String deniedPage() {
        return "denied";
    }

    /**
     * метод проверяет существует ли юзер под таким email и если да , то высылает ссылку подтверждение
     * @param email на который высылается ссылка - подтверждение для сброса игенерации нового пароля
     * @return редирект на страницу логина
     */
    @PostMapping("/restorePassword")
    public String loginRestore(@RequestParam("login_username") String email){
        User user = userService.findByEmail(email).orElseThrow(UserNotFoundException::new);
        userService.sendConfirmationTokenToResetPassword(user);
        return "redirect:/login";
    }


}
