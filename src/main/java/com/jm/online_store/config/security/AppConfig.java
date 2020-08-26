package com.jm.online_store.config.security;

import com.jm.online_store.config.security.odnoklassniki.OAuth2Odnoklassniki;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public OAuth2Odnoklassniki oAuth2Odnoklassniki() {
        return new OAuth2Odnoklassniki();
    }
}
