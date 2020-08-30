package com.jm.online_store.config.security.Twitter;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;

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
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Класс OAuth 1.0 авторизации через Twitter API
 */
@NoArgsConstructor
@AllArgsConstructor
@Service
public class TwitterAuth {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    public static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";
    private OAuth1RequestToken requestToken;
    private OAuth10aService service;

    @SuppressWarnings("PMD.SystemPrintln")
    public String twitterAuth() throws InterruptedException, ExecutionException, IOException {
        service = new ServiceBuilder("BiCDQUyByE72SHyBkqJPe1wAi")
                .apiSecret("KlJocOjZNnLodCExCNfsQbaz3utxjhOoc7rFuUwncgolAgQnEA")
                .callback("http://localhost:9999" + "/oauthTwitter")
                .build(TwitterApi.instance());

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        requestToken = service.getRequestToken();
        System.out.println("Now go and authorize ScribeJava here:");
        return service.getAuthorizationUrl(requestToken);
    }


    public void getAccessToken(String oauth_verifier) throws InterruptedException, ExecutionException, IOException {
        final String oauthVerifier = oauth_verifier;

        // Trade the Request Token and Verifier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);

        // Now let's go and ask for a protected resource!
        System.out.println("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
        service.signRequest(accessToken, request);
        String userFromTwitter = service.execute(request).getBody();
        System.out.println(userFromTwitter);

        String email = "test@example.ru";
        String name = null;
        String screen_name = null;
        String description = null;
        String location = null;
        String profile_image_url = null;

        String[] words = userFromTwitter.split(",");
        for (String pair : words) {
            if (pair.contains("name")) {
                name = pair.substring(pair.indexOf(":") + 2, pair.length() - 2);
            }
            if (pair.contains("screen_name")) {
                screen_name = pair.substring(pair.indexOf(":") + 2, pair.length() - 1);
            }
            if (pair.contains("description")) {
                description = pair.substring(pair.indexOf(":") + 2, pair.length() - 1);
            }
            if (pair.contains("location")) {
                location = pair.substring(pair.indexOf(":") + 2, pair.length() - 1);
            }
            if (pair.contains("profile_image_url")) {
                profile_image_url = pair.substring(pair.indexOf(":") + 2, pair.length() - 1);
            }
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setRoles(Collections.singleton(roleService.findByName("ROLE_CUSTOMER").get()));
        newUser.setFirstName(name);
        newUser.setLastName(screen_name);
        newUser.setPassword("1");

        User user = userService.findByEmail(email).orElseGet(() -> {
            userService.addUser(newUser);
            return newUser;
        });

        Authentication customAuthentication = new UsernamePasswordAuthenticationToken(
                user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(customAuthentication);
    }
}
