package com.jm.online_store.config.security.Twitter;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Класс OAuth 1.0 авторизации через Twitter API
 */
@NoArgsConstructor
@AllArgsConstructor
@Service
public class TwitterAuth {
    protected Integer appId = 18634122;
    protected String apiKey = "BiCDQUyByE72SHyBkqJPe1wAi";
    protected String apiKeySecret = "KlJocOjZNnLodCExCNfsQbaz3utxjhOoc7rFuUwncgolAgQnEA";
    protected String accessToken = "1298275136731258881-nzPoBgZQvRPPyxn778Wmj3p0Jkdn5L";
    protected String accessTokenSecret = "OCSM0TgL8ymQsshEUx9RAhKRwNzYaY4g4mIqEiLf1QW0y";

    String query = "https://api.twitter.com/oauth/request_token";


    public void Http() throws IOException {
        String webPage = "https://api.twitter.com/oauth/request_token";
        String name = "admin";
        String password = "admin";

        String authString = name + ":" + password;
        System.out.println("auth string: " + authString);
        String authEncBytes = DatatypeConverter.printBase64Binary(authString.getBytes());
        String authStringEnc = new String(authEncBytes);
        System.out.println("Base64 encoded auth string: " + authStringEnc);


        URL url = new URL(webPage);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
        urlConnection.connect();

        System.out.println(urlConnection.getRequestProperty("Authorization"));
    }


}



