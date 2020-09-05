package com.jm.online_store.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigListener implements ApplicationListener<ContextRefreshedEvent> {

    @Value("${spring.security.oauth2.client.registration.facebook.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.facebook.client-secret}")
    private String clientSecret;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.debug("spring.security.oauth2.client.registration.facebook.client-id: {}", clientId);
        log.debug("spring.security.oauth2.client.registration.facebook.client-secret: {}", clientSecret);
    }
}
