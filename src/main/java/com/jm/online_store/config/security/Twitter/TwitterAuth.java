package com.jm.online_store.config.security.Twitter;

import com.github.scribejava.apis.TwitterApi;
import com.github.scribejava.core.builder.ServiceBuilder;
import com.github.scribejava.core.model.OAuth1AccessToken;
import com.github.scribejava.core.model.OAuth1RequestToken;
import com.github.scribejava.core.model.OAuthRequest;
import com.github.scribejava.core.model.Response;
import com.github.scribejava.core.model.Verb;
import com.github.scribejava.core.oauth.OAuth10aService;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * Класс OAuth 1.0 авторизации через Twitter API
 */
@NoArgsConstructor
@Service
public class TwitterAuth {

    public static final String PROTECTED_RESOURCE_URL = "https://api.twitter.com/1.1/account/verify_credentials.json";
    public String code;
    OAuth1RequestToken requestToken;
    OAuth10aService service;


    @SuppressWarnings("PMD.SystemPrintln")
    public String twitterAuth() throws InterruptedException, ExecutionException, IOException {
        service = new ServiceBuilder("BiCDQUyByE72SHyBkqJPe1wAi")
                .apiSecret("KlJocOjZNnLodCExCNfsQbaz3utxjhOoc7rFuUwncgolAgQnEA")
                .callback("http://localhost:9999" + "/oauth1")
                .build(TwitterApi.instance());
        final Scanner in = new Scanner(System.in);

        // Obtain the Request Token
        System.out.println("Fetching the Request Token...");
        requestToken = service.getRequestToken();
        System.out.println("Got the Request Token!");
        System.out.println();

        System.out.println("Now go and authorize ScribeJava here:");
        return service.getAuthorizationUrl(requestToken);
    }

    public void getAccessToken(String token) throws InterruptedException, ExecutionException, IOException {
        final String oauthVerifier = code;

        // Trade the Request Token and Verifier for the Access Token
        System.out.println("Trading the Request Token for an Access Token...");
        final OAuth1AccessToken accessToken = service.getAccessToken(requestToken, oauthVerifier);
        System.out.println("Got the Access Token!");
        System.out.println("(The raw response looks like this: " + accessToken.getRawResponse() + "')");
        System.out.println();


//
//        // Now let's go and ask for a protected resource!
//        System.out.println("Now we're going to access a protected resource...");
//        final OAuthRequest request = new OAuthRequest(Verb.GET, PROTECTED_RESOURCE_URL);
//        service.signRequest(accessToken, request);
//        try (Response response = service.execute(request)) {
//            System.out.println("Got it! Lets see what we found...");
//            System.out.println();
//            System.out.println(response.getBody());
//        }
//        System.out.println();
//        System.out.println("That's it man! Go and build something awesome with ScribeJava! :)");

    }

    public void codeResp(String code) {
        this.code = code;
    }
}



