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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
@Service
@Slf4j
@RequiredArgsConstructor
public class TwitterAuth {
    private final UserService userService;
    private final RoleService roleService;

    @Value("${spring.server.url}")
    private String serverUrl;

    @Value("${spring.oauth1a.twitter.public_key}")
    private String publicKey;

    @Value("${spring.oauth1a.twitter.secret_key}")
    private String secretKey;

    @Value("${spring.oauth1a.twitter.resource_url}")
    private String resourceUrl;

    private OAuth1RequestToken requestToken;
    private OAuth10aService service;

    public String twitterAuth() throws InterruptedException, ExecutionException, IOException {
        service = new ServiceBuilder(publicKey)
                .apiSecret(secretKey)
                .callback(serverUrl + "/oauthTwitter")
                .build(TwitterApi.instance());
        // Obtain the Request Token
        log.debug("Fetching the Request Token...");
        requestToken = service.getRequestToken();
        return service.getAuthorizationUrl(requestToken);
    }

    public void getAccessToken(String oauth_verifier) throws InterruptedException, ExecutionException, IOException {
        // Trade the Request Token and Verifier for the Access Token
        log.debug("Trading the Request Token for an Access Token...");
        final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauth_verifier);

        // Now let's go and ask for a protected resource!
        log.debug("Now we're going to access a protected resource...");
        final OAuthRequest request = new OAuthRequest(Verb.GET, resourceUrl);
        service.signRequest(accessToken, request);
        String userFromTwitter = service.execute(request).getBody();
        log.debug(userFromTwitter);

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
        }
    }
}
