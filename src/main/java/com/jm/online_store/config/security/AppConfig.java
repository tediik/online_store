package com.jm.online_store.config.security;

import com.jm.online_store.config.security.odnoklassniki.OAuth2Service;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public OAuth2Service oAuth2Service() {
        return new OAuth2Service();
    }
}
