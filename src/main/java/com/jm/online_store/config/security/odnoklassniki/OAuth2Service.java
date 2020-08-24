package com.jm.online_store.config.security.odnoklassniki;

import com.github.scribejava.apis.OdnoklassnikiApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.jm.online_store.model.User;
import com.jm.online_store.service.interf.RoleService;
import com.jm.online_store.service.interf.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Класс OAuth авторизации через Odnoklassniki API
 */
@NoArgsConstructor
@AllArgsConstructor
@Service
public class OAuth2Service {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    private static final String NETWORK_NAME = "Odnoklassniki.ru";
    private static final String PROTECTED_RESOURCE_URL
            = "https://api.ok.ru/api/users/getCurrentUser?application_key=CINLIMJGDIHBABABA&format=JSON";

    private final String clientId = "512000494295";
    private final String publicKey = "CINLIMJGDIHBABABA";
    private final String secretKey = "2DD0271520909F838004E332";
    
    private final OAuth20Service service = new ServiceBuilder(clientId)
            .apiSecret(secretKey)
            .callback("http://localhost:9999/oauth")
            .defaultScope("GET_EMAIL")
            .build(OdnoklassnikiApi.instance());

    public String getAuthorizationUrl() {
        return service.getAuthorizationUrl();
    }

    public void UserAuth(String token) {
        try {
            OAuth2AccessToken accessToken = service.getAccessToken(token);
            accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
            OAuthRequest request = new OAuthRequest(Verb.GET, String.format(PROTECTED_RESOURCE_URL, publicKey));
            service.signRequest(accessToken, request);
            String userFromOK = service.execute(request).getBody();

            System.out.println(userFromOK);
            String email = null;
            String firstName = null;
            String lastName = null;
            String gender = null;
            String birthday = null;

            String[] words = userFromOK.split(",");
            for (String pair : words) {
                if (pair.contains("email")) {
                    email = pair.substring(pair.indexOf(":") + 2, pair.length() - 2);
                }
                if (pair.contains("first_name")) {
                    firstName = pair.substring(pair.indexOf(":") + 2, pair.length() - 1);
                }
                if (pair.contains("last_name")) {
                    lastName = pair.substring(pair.indexOf(":") + 2, pair.length() - 1);
                }
                if (pair.contains("gender")) {
                    gender = pair.substring(pair.indexOf(":") + 2, pair.length() - 1);
                }
                if (pair.contains("\"birthday\":")) {
                    birthday = pair.substring(pair.indexOf(":") + 2, pair.length() - 1);
                }
            }

            User newUser = new User();
            newUser.setEmail(email);
            newUser.setRoles(Collections.singleton(roleService.findByName("ROLE_CUSTOMER").get()));
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setPassword("1");
            newUser.setBirthdayDate(LocalDate.parse(birthday));
            if (gender.equals("male")) {
                newUser.setUserGender(User.Gender.MAN);
            } else {
                newUser.setUserGender(User.Gender.WOMAN);
            }

            User user = userService.findByEmail(email).orElseGet(() -> {
                userService.addUser(newUser);
                return newUser;
            });

            Authentication customAuthentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(customAuthentication);

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
