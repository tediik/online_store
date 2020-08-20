package com.jm.online_store.config.security.odnoklassniki;

import com.github.scribejava.apis.OdnoklassnikiApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth2AccessToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth20Service;
import com.jm.online_store.model.User;
import com.jm.online_store.service.RoleService;
import com.jm.online_store.service.UserService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class OAuth2Service {

    private UserService userService;

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
            String string = userFromOK.substring(userFromOK.lastIndexOf(":") + 2);
            String email = string.substring(0, string.length() - 2);
            System.out.println(email);
            User user = new User();
            user.setEmail(email);
            user.setRoles(Collections.singleton(roleService.findByName("ROLE_CUSTOMER").get()));

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
