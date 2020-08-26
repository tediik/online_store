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
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
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
public class OAuth2Odnoklassniki {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Value("${spring.server.url}")
    private String serverUrl;

    @Value("${spring.oauth2.odnoklassniki.resource_url}")
    private String resourceUrl;

    @Value("${spring.oauth2.odnoklassniki.client_id}")
    private String clientId;

    @Value("${spring.oauth2.odnoklassniki.public_key}")
    private String publicKey;

    @Value("${spring.oauth2.odnoklassniki.secret_key}")
    private String secretKey;

    public OAuth20Service serviceBuilder() {
        return new ServiceBuilder(clientId)
                .apiSecret(secretKey)
                .callback(serverUrl + "/oauth")
                .defaultScope("GET_EMAIL")
                .build(OdnoklassnikiApi.instance());
    }

    public String getAuthorizationUrl() {
        return serviceBuilder().getAuthorizationUrl();
    }

    public void UserAuth(String token) {
        try {
            OAuth20Service service = serviceBuilder();
            OAuth2AccessToken accessToken = service.getAccessToken(token);
            accessToken = service.refreshAccessToken(accessToken.getRefreshToken());
            OAuthRequest request = new OAuthRequest(Verb.GET, String.format(resourceUrl, publicKey));
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
