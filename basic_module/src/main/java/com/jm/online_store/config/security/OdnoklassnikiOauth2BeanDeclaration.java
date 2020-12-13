package com.jm.online_store.config.security;

import com.jm.online_store.config.security.odnoklassniki.OAuth2Odnoklassniki;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * The type Odnoklassniki oauth 2 bean declaration.
 */
@Configuration
public class OdnoklassnikiOauth2BeanDeclaration {

    @Bean
    public OAuth2Odnoklassniki oAuth2Odnoklassniki() {
        return new OAuth2Odnoklassniki();
    }
}
